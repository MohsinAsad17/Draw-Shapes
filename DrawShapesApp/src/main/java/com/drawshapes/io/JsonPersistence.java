package com.drawshapes.io;

import com.drawshapes.model.Shape;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonPersistence {
    private final ObjectMapper mapper;

    public JsonPersistence() {
        this.mapper = new ObjectMapper();
    }

    public void save(File file, List<Shape> shapes) throws IOException {
        mapper.writeValue(file, shapes);
    }

    public List<Shape> load(File file) throws IOException {
        return mapper.readValue(file, new TypeReference<List<Shape>>() {});
    }
}
