package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Service;

/**
 * A mapper that takes a string and maps it into a Service object
 */
public class ServiceMapper {
    public Service map(String input) {
        Gson gson = new GsonBuilder().create();
        Service service = gson.fromJson(input, Service.class);
        return service;
    }
}
