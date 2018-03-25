package jfskora.rest;

import spark.Request;
import spark.Response;

import static spark.Spark.get;
import static spark.Spark.port;

public class TestRest {
    public static void main(String[] args) {
        port(8080);
        get("/rest", (Request request, Response response) -> {
            response.raw().getWriter().write("Hello Writer World!");
            return response;
        });
    }

}
