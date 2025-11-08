package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ScanServlet", urlPatterns = "/*", loadOnStartup = 1)
public class ScanServlet extends HttpServlet {

    private List<Class<?>> classes;

    @Override
    public void init() throws ServletException {
        System.out.println("==== Initialisation du scanner d'annotations ====");

        String packageName = "org.example.controllers";
        AnnotationScanner scanner = new AnnotationScanner();
        classes = scanner.findAllClasses(packageName);

        System.out.println("==== Fin du scan ====");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();
        if (path == null) path = "/";

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><body style='font-family: Arial'>");
        out.println("<h2>Résultat du mapping pour URL : " + path + "</h2><hr>");

        boolean found = false;

        for (Class<?> clazz : classes) {

            if (clazz.isAnnotationPresent(annotationType.class)) {
                annotationType classAnnotation = clazz.getAnnotation(annotationType.class);

                for (Method method : clazz.getDeclaredMethods()) {

                    if (method.isAnnotationPresent(Annotation.class)) {
                        org.example.Annotation methodAnnotation = method.getAnnotation(org.example.Annotation.class);

                        System.out.println("DEBUG: URL navigateur = '" + path + "'");
                        System.out.println("DEBUG: Méthode = '" + method.getName() + "', path annotation = '" + methodAnnotation.path() + "'");

                        if (methodAnnotation.path().equals(path)) {
                            found = true;

                            out.println("<p><b>Classe :</b> " + clazz.getName() + "<br>");
                            out.println("<b>Annotation classe (url) :</b> " + classAnnotation.url() + "<br>");
                            out.println("<b>Méthode :</b> " + method.getName() + "<br>");
                            out.println("<b>Annotation méthode (path) :</b> " + methodAnnotation.path() + "</p><hr>");
                        }
                    }
                }
            }
        }

        if (!found) {
            out.println("<p style='color:red;'>Aucune méthode trouvée pour l'URL : " + path + "</p>");
        }

        out.println("</body></html>");
    }

}
