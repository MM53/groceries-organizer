package org.example.repositories;

import org.example.entities.aggregateRoots.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository {

    public void save(Tag tag);

    public Optional<Tag> findTagByName(String name);

    public List<Tag> getAll();

    public void delete(Tag tag);
}
