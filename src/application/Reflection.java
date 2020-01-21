package application;

import logic.Game;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {

    public static void main() {
        SettingsController obj = new SettingsController();
       // Game game = new Game();


        try (FileWriter writer = new FileWriter("reflection.txt");
             BufferedWriter bw = new BufferedWriter(writer)) {

            // Creating class object from the object using
            // getclass method
            Class cls = obj.getClass();
           // Class clz = game.getClass();
            bw.write("\nThe name of class is " +
                    cls.getName());
           /* bw.write("\nThe name of class is " +
                    clz.getName());*/
            // Getting the constructor of the class through the
            // object of the class
            Constructor constructor = null;
           // Constructor constructor1=null;
            try {
                constructor = cls.getConstructor();
               // constructor1 = clz.getConstructor();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            bw.write("\nThe name of constructor is " +
                    constructor.getName());
            /*bw.write("\nThe name of constructor is " +
                    constructor1.getName());*/

            // Getting methods of the class through the object
            // of the class by using getMethods
            Method[] methods = cls.getMethods();
            //Method[] methods1 = clz.getMethods();

            // Printing method names

            for (Method method : methods) {
                bw.write("\nThe public methods of class are:" + method.getName());
                Field[] fields = cls.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    bw.write("\nThe field is:" + fields[i].toString());

                }


            }
          /*  for (Method method1 : methods) {
                bw.write("\nThe public methods of class are:" + method1.getName());
                Field[] fields = clz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    bw.write("\nThe field is:" + fields[i].toString());

                }


            }*/
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }
}
