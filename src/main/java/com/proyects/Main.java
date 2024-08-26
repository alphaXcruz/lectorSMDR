package com.proyects;

import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        Stack<Llamada> stack = new Stack<>();

        Server server = new Server(stack);
        server.startServer();

        DatabaseInserter inserter = new DatabaseInserter(stack);

        inserter.insertData();

    }
}
