package world.thismagical.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.xml.bind.DatatypeConverter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

    // this is easier than making sure that the server is set to the correct locale
    public static final List<String> monthsEn = Arrays.asList("January", "February", "March", "April", "May","June","July","August","September","October","November","December");
    public static final List<String> daysEn = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");

    public static void handleException(Exception ex){
        log(ex.getMessage() + "\r\n" + getStackTraceStr(ex));
    }

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

        String[] supportedExtensions = {"jpeg","jpg","png","bmp"};

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

    public static ZonedDateTime getGmt(LocalDateTime localDateTime){

        ZonedDateTime ldtZoned = localDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime gmt = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));

        return gmt;
    }

    public static String formatDate(LocalDateTime localDateTime){

        if (localDateTime == null){
            return "";
        }

        ZonedDateTime gmt = getGmt(localDateTime);
        String gmtFormat = gmt.format(DateTimeFormatter.ofPattern("dd LLLL yyyy HH:mm")) + " GMT";

        // 21 luglio 2020 20:44 GMT - that might happen if server is somewhere in the world...
        String[] gmtTokens = gmtFormat.split(" ");
        gmtTokens[1] = monthsEn.get(gmt.getMonthValue()-1);

        return String.join(" ", gmtTokens);
    }

    public static String formatPubDate(LocalDateTime localDateTime){

        if (localDateTime == null){
            return "";
        }

        ZonedDateTime gmt = getGmt(localDateTime);
        String gmtFormat = gmt.format(DateTimeFormatter.ofPattern("dd LLLL yyyy HH:mm:ss")) + " GMT";

        // 21 luglio 2020 20:44 GMT - that might happen if server is somewhere in the world...
        String[] gmtTokens = gmtFormat.split(" ");
        gmtTokens[1] = monthsEn.get(gmt.getMonthValue()-1);

        String preRes = String.join(" ", gmtTokens);
        return daysEn.get(gmt.getDayOfWeek().getValue()-1) + ", " + preRes;
    }

    public static String nullToEmpty(String input){
        if (input == null){
            return "";
        }

        return input;
    }

    public static String getPath(String path){
            if (!path.endsWith("\\") && !path.endsWith("/")){
                return path + "/";
            } else {
                return path;
            }
    }

    public static Boolean isValidGeo(String geo){

        if (geo == null){
            return false;
        }

        if (geo.isEmpty()){
            return false;
        }

        // ^\s*\-?[0-9]{1,2}(\.[0-9]{1,})?\,\s+\-?[0-9]{1,3}(\.[0-9]{1,})?\s*$
        String geoRegExp = "^\\s*\\-?[0-9]{1,2}(\\.[0-9]{1,})?\\,\\s+\\-?[0-9]{1,3}(\\.[0-9]{1,})?\\s*$";
        return geo.matches(geoRegExp);
    }

    public static boolean strIsNumber(String input){
        if (input == null || input.isEmpty()){
            return false;
        }

        return input.matches("^[0-9]+$");
    }

    public static String normalizeURL(String input){
        if (!input.startsWith("http://") && !input.startsWith("https://")){
            input = "http://" + input;
        }

        if (input.endsWith("/")){
            input = input.substring(0, input.length()-1);
        }

        return input;
    }

    public static String htmlSingleQuote(String input){

        if (input == null){
            return "";
        }

        return input.replaceAll("'", "&apos;");
    }

    public static String escapeLtGt(String input){
        if (input == null){
            return "";
        }

        return input.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
