package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet(name = "frontServlet", urlPatterns = "/*", loadOnStartup = 1)
public class frontServlet extends HttpServlet {

    RequestDispatcher defaultDispatcher;
    private List<Class<?>> classes;
    private String message ="";

    @Override
    public void init() {
        System.out.println("==== Initialisation du scanner d'annotations ====");

        String packageName = "org.example.controllers";
        AnnotationScanner scanner = new AnnotationScanner();
        classes = scanner.findAllClasses(packageName);

        defaultDispatcher = getServletContext().getNamedDispatcher("default");
        System.out.println("==== Fin du scan ====");
        message="";
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        
        boolean resourceExists = getServletContext().getResource(path) != null;

        if (resourceExists) {
            defaultServe(req, res);
        } else {
            super.service(req, res);
        }
    }

    private void customServe(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try (PrintWriter out = res.getWriter()) {
            String url = req.getRequestURI();
            res.setContentType("text/html;charset=UTF-8");
            out.println("<html><head><title>FrontServlet</title></head><body>");
            out.println("<h1>URL demandée : " + url + "</h1>");
            out.println("<h1>message : " + message + "</h1>");
            out.println("</body></html>");
        }
        message="";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String path = request.getPathInfo();
        if (path == null) path = "/";

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
                            try {
                                Object instance = clazz.getDeclaredConstructor().newInstance();
                                Method method1 = method;
                                // out.println("<p><b>Classe :</b> " + clazz.getName() + "<br>");
                                // out.println("<b>Annotation classe (url) :</b> " + classAnnotation.url() + "<br>");
                                // out.println("<b>Méthode :</b> " + method.getName() + "<br>");
                                // out.println("<b>Annotation méthode (path) :</b> " + methodAnnotation.path() + "</p><hr>");
                                if (method1.getReturnType().getName().equals("java.lang.String")) {
                                    message +="<h2>Résultat du mapping pour URL : " + path + "</h2><hr>";
                                    message +="<p>Valeur retournée par la méthode :</p><h5>" + method1.invoke(instance) + "</h5>";
                                    customServe(request, response);
                                }
                                else if (method1.getReturnType().equals(ModelView.class)) {
                                    Object resultat = method1.invoke(instance);
                                    org.example.ModelView modelView = (org.example.ModelView) resultat;
                                    String page = modelView.getPage();
                                    request.getRequestDispatcher("/WEB-INF/pages/" + page + ".jsp").forward(request, response);
                                    return;
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                }
            }
        }

        if (!found) {
            message+="<h2>Résultat du mapping pour URL : " + path + "</h2><hr>";
            message+="<p style='color:red;'>Aucune méthode trouvée pour l'URL : " + path + "</p>";
            customServe(request, response);
        }
    }

    private void defaultServe(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (defaultDispatcher != null) {
            defaultDispatcher.forward(req, res);
        } else {
            res.sendError(404, "Default dispatcher introuvable");
        }
    }
}
