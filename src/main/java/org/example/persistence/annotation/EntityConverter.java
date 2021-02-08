package org.example.persistence.annotation;

import com.google.auto.service.AutoService;
import org.springframework.util.StringUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SupportedAnnotationTypes("org.example.persistence.annotation.GenerateConverter")
@SupportedSourceVersion(SourceVersion.RELEASE_15)
@AutoService(Processor.class)
public class EntityConverter extends AbstractProcessor {

    private List<String> allEntities;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {

            allEntities = roundEnv.getElementsAnnotatedWith(annotation)
                                  .stream()
                                  .map(element -> ((TypeElement) element).getQualifiedName().toString())
                                  .collect(Collectors.toList());

            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(annotation)) {

                String className = ((TypeElement) annotatedElement).getQualifiedName().toString();
                String aggregateClass = annotatedElement.getAnnotation(GenerateConverter.class).aggregate();

                List<Element> fields = annotatedElement.getEnclosedElements()
                                                       .stream()
                                                       .filter(e -> e.getAnnotation(ConvertValue.class) != null)
                                                       .collect(Collectors.toList());
                try {
                    writeConverterFile(className, aggregateClass, fields);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    private void writeConverterFile(String className,
                                    String aggregateClass,
                                    List<Element> fields) throws IOException {

        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String aggregateSimpleName = aggregateClass.substring(aggregateClass.lastIndexOf(".") + 1);
        String converterClassName = className + "Converter";
        String converterSimpleClassName = converterClassName.substring(lastDot + 1);

        JavaFileObject converterFile = processingEnv.getFiler()
                                                    .createSourceFile(converterClassName);

        try (PrintWriter out = new PrintWriter(converterFile.openWriter())) {

            if (packageName != null) {
                out.println("package " + packageName + ";");
                out.println();
            }

//            out.println("/*\n" + comments + "\n*/");

            out.println("import " + aggregateClass + ";");
            out.println(generateImports(fields));

            out.print("public class ");
            out.print(converterSimpleClassName);
            out.println(" {");
            out.println();

            out.println(generateConvertFromEntity(simpleClassName, aggregateSimpleName, fields));
            out.println(generateConvertToEntity(simpleClassName, aggregateSimpleName, fields));

            out.println("}");
        }
    }

    private String generateImports(List<Element> fields) {
        List<String> types = fields.stream()
                                   .map(Element::asType)
                                   .filter(type -> !type.getKind().isPrimitive())
                                   .flatMap(this::getRequiredTypes)
                                   .distinct()
                                   .collect(Collectors.toList());

        return types.stream()
                    .map(type -> "import " + type + ";\n")
                    .collect(Collectors.joining());
    }

    private Stream<String> getRequiredTypes(TypeMirror typeMirror) {
        String type = typeMirror.toString();
        List<String> requiredTypes = new ArrayList<>();
        if (type.startsWith(Set.class.getName())) {
            requiredTypes.add(Set.class.getName());
            requiredTypes.add(Collectors.class.getName());

            type = type.substring(type.indexOf("<") + 1);
            type = type.substring(0, type.indexOf(">"));
        }
        if (allEntities.contains(type)) {
            requiredTypes.add(type + "Converter");
        }
        requiredTypes.add(type);
        return requiredTypes.stream();
    }

    private String generateConvertFromEntity(String entityClass, String aggregateClass, List<Element> fields) {
        String method = "public static " + aggregateClass + " convertFromEntity (" + entityClass + " entity){\n";
        method += "return new " + aggregateClass + "(" + fields.stream().map(this::generateFieldConverter).collect(Collectors.joining(",\n")) + ");\n";
        method += "}\n";
        return method;
    }

    private String generateFieldConverter(Element field) {
        String type = field.asType().toString();
        String converterName = type.substring(type.lastIndexOf(".") + 1).replace(">", "") + "Converter";
        String valueExtraction = "entity." + getGetterName(field) + "()";
        if (allEntities.contains(type)) {
            valueExtraction = converterName + ".convertFromEntity(" + valueExtraction + ")";
        } else if (type.startsWith(Set.class.getName())) {
            valueExtraction = valueExtraction + ".stream().map(" + converterName + "::convertFromEntity).collect(Collectors.toSet())";
        }
        return valueExtraction;
    }

    private String generateConvertToEntity(String entityClass, String aggregateClass, List<Element> fields) {
        String method = "public static " + entityClass + " convertToEntity (" + aggregateClass + " aggregate){\n";
        method += entityClass + " entity = new " + entityClass + "();\n";
        method += fields.stream()
                        .map(field -> "entity." + getSetterName(field) + "(" + generateConvertToEntityValueExtraction(field) + ");\n")
                        .collect(Collectors.joining());
        method += "return entity;\n";
        method += "}\n";
        return method;
    }

    private String generateConvertToEntityValueExtraction(Element field) {
        String type = field.asType().toString();
        String converterName = type.substring(type.lastIndexOf(".") + 1).replace(">", "") + "Converter";
        String valueExtraction = "aggregate." + getAggregateGetterName(field) + "()";
        if (allEntities.contains(type)) {
            valueExtraction = converterName + ".convertToEntity(" + valueExtraction + ")";
        } else if (type.startsWith(Set.class.getName())) {
            valueExtraction = valueExtraction + ".stream().map(" + converterName + "::convertToEntity).collect(Collectors.toSet())";
        }
        return valueExtraction;
    }

    private static String getSetterName(Element field) {
        String configuredSetter = field.getAnnotation(ConvertValue.class).setter();
        if (!configuredSetter.equals("")) {
            return configuredSetter;
        }
        return "set" + StringUtils.capitalize(field.getSimpleName().toString());
    }

    private static String getGetterName(Element field) {
        String configuredGetter = field.getAnnotation(ConvertValue.class).getter();
        if (!configuredGetter.equals("")) {
            return configuredGetter;
        }
        return "get" + StringUtils.capitalize(field.getSimpleName().toString());
    }

    private static String getAggregateGetterName(Element field) {
        String configuredGetter = field.getAnnotation(ConvertValue.class).aggregateGetter();
        if (!configuredGetter.equals("")) {
            return configuredGetter;
        }
        return "get" + StringUtils.capitalize(field.getSimpleName().toString());
    }
}
