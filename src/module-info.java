module Projektan {
    requires javafx.fxml;
    requires javafx.controls;
    requires Tools;
    requires java.rmi;
    requires java.logging;
    requires java.xml;
    requires java.naming;
    requires java.desktop;

    opens application;
    opens images;
    opens logic;
    opens threads;
    opens chat;
    opens sockets;
}