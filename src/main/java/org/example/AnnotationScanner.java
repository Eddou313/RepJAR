package org.example;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import java.lang.reflect.Method;
import org.example.*;

public class AnnotationScanner {

    public AnnotationScanner(){}

    public List<Class<?>> findAllClasses(String packageName) { 
        List<Class<?>> annotatedClasses = new ArrayList<>(); 

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); 
        String path = packageName.replace('.', '/');
        
        try {
            System.out.println("Recherche du package: " + packageName);
            System.out.println("Chemin: " + path);
            
            Enumeration<URL> resources = classLoader.getResources(path); 

            if (!resources.hasMoreElements()) {
                System.out.println("Aucune ressource trouvée pour: " + path);
                return annotatedClasses;
            }

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                System.out.println(" Ressource trouvée: " + resource.getFile());
                
                File directory = new File(resource.getFile());

                if (directory.exists()) {
                    System.out.println(" Scan du dossier: " + directory.getAbsolutePath());
                    scanDirectory(directory, packageName, annotatedClasses); 
                } else {
                    System.out.println(" Dossier non accessible: " + directory.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.out.println(" Erreur lors de la recherche des ressources: " + e.getMessage());
            e.printStackTrace();
        }
        
        return annotatedClasses;
    }

    public void scanDirectory(File directory, String packageName, List<Class<?>> annotatedClasses) { 
        File[] files = directory.listFiles();
        if (files == null) {
            System.out.println(" Dossier vide: " + directory.getAbsolutePath());
            return;
        }

        System.out.println(" Fichiers trouvés: " + files.length);
        
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println(" Sous-dossier: " + file.getName());
                scanDirectory(file, packageName + "." + file.getName(), annotatedClasses);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                System.out.println(" Traitement de: " + className);
                
                try {
                    Class<?> clazz = Class.forName(className);

                    if (clazz.isAnnotationPresent(annotation.class)) {
                        System.out.println(" CLASSE ANNOTÉE TROUVÉE: " + className);
                        annotatedClasses.add(clazz);
                    } else {
                        System.out.println(" Pas d'annotation: " + className);
                    }
                } catch (Exception e) {
                    System.out.println("  Erreur avec la classe " + className + ": " + e.getMessage());
                }
            } else {
                System.out.println(" Fichier ignoré: " + file.getName());
            }
        }
    }
}