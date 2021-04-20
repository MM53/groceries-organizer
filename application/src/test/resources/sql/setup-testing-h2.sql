CREATE SCHEMA IF NOT EXISTS public;
SET SCHEMA public;
RUNSCRIPT FROM 'classpath:sql/item.sql';
RUNSCRIPT FROM 'classpath:sql/storedItem.sql';
RUNSCRIPT FROM 'classpath:sql/shoppingList.sql'