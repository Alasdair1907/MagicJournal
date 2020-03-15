package world.thismagical.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import world.thismagical.entity.TestEntity;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.xml.bind.DatatypeConverter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

    public static String getStackTraceStr(Exception ex){
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static SessionFactory getSessionfactory(){
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();

        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        return factory;
    }

    public static Session getNewSession(){
        SessionFactory factory = getSessionfactory();
        return factory.openSession();
    }

    public static Session getNewSession(SessionFactory sessionFactory){
        return sessionFactory.openSession();
    }

    public static void log(String message){
        System.out.println("["+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] "+message+"\r\n");
    }

    public static String sha256(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digestBytes = messageDigest.digest(input.getBytes("UTF-8"));
            return DatatypeConverter.printHexBinary(digestBytes);
        } catch (Exception e){
            throw new IllegalArgumentException("cannot create sha256 hash");
        }
    }

    public static Short int2short(Integer input){
        return input.shortValue();
    }

    public static Long int2long(Integer input){
        return input.longValue();
    }

    public static String getExtension(String fName){

        String[] supportedExtensions = {"jpeg","jpg","png"};

        String extensionPattern = ".*\\.([a-zA-Z]+)$";

        Pattern pattern = Pattern.compile(extensionPattern);
        Matcher matcher = pattern.matcher(fName);
        String extension = null;

        if (matcher.find()){
            extension = matcher.group(1);
        } else {
            return null;
        }

        List<String> extensionsList = Arrays.asList(supportedExtensions);
        if (!extensionsList.contains(extension.toLowerCase())){
            Tools.log("[WARN] Unsupported extension for filename: "+fName);
            return null;
        }

        return extension;
    }

    public static String getContentTypeByExtension(String extension){

        if (extension == null){
            return null;
        }

        String extLower = extension.toLowerCase();

        if (extLower.equals("jpeg") || extLower.equals("jpg")){
            return "image/jpeg";
        } else if (extLower.equals("png")) {
            return "image/png";
        }

        return null;
    }

    public static Boolean verifyFileName(String fName){
        String regex = "[a-zA-Z0-9\\-]+[_a-zA-Z]*\\.[a-zA-Z]{3,7}";

        if (!fName.matches(regex)){
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    public static String nullToEmpty(String input){
        if (input == null){
            return "";
        }

        return input;
    }
}
