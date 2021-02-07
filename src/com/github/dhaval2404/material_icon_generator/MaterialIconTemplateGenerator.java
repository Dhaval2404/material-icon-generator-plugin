package com.github.dhaval2404.material_icon_generator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Generate Template file for Material MaterialIcon Generator
 * <p>
 * It will generate resources/template.xml
 * <p>
 * <?xml version="1.0"?>
 * <icons default="action/ic_3d_rotation">
 * <option id="action/ic_3d_rotation">action/ic_3d_rotation</option>
 * </icons>
 */
public class MaterialIconTemplateGenerator {

    public static void main(String[] args) throws IOException {
        //Get MaterialIcon Pack
        Map<String, String> iconPack = getIconTemplate();
        Document element = generateTemplate(iconPack);

        XMLOutputter xmlOutput = new XMLOutputter();
        // display nice nice
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(element, new FileWriter("resources/template.xml"));

        System.out.println("File Saved!");
    }

    private static Document generateTemplate(Map<String, String> iconPack) {
        //Create main icons Element
        Element mainElement = new Element("icons");
        Document doc = new Document();
        doc.setRootElement(mainElement);

        boolean setDefault = true;
        Element iconElement;

        //Iterate categories
        for (String iconName : iconPack.keySet()) {
            if (setDefault) {
                //set first icon as default
                mainElement.setAttribute("default", iconName);
                //after setting default make sure this condition never satisfied
                setDefault = false;
            }

            //Create child element with name as "option"
            iconElement = new Element("option");

            //set =>>id="action/ic_3d_rotation"
            iconElement.setAttribute("id", iconName);
            iconElement.setAttribute("version", iconPack.get(iconName));

            //set value
            iconElement.setText(iconName);

            //Append to main icons element
            //mainElement.addContent(iconElement);
            doc.getRootElement().addContent(iconElement);
        }
        return doc;
    }

    /**
     * Parse Icon JSON
     */
    public static Map<String, String> getIconTemplate() throws IOException {
        Map<String, Object> iconLists = fetchIconContents();

        Map<String, String> iconInfo = new TreeMap<>();
        if (iconLists == null) return iconInfo;

        ArrayList<Object> icons = (ArrayList<Object>) iconLists.get("icons");
        for (Object icon : icons) {
            Map<String, Object> iconData = ((Map<String, Object>) icon);
            List<String> categories = (List<String>) iconData.get("categories");

            for (String category : categories) {
                //Create icon name. e.g. action/ic_3d_rotation
                String iconId = category + "/" + iconData.get("name").toString();
                iconInfo.put(iconId, String.valueOf((int) (double) iconData.get("version")));
            }
        }

        return iconInfo;
    }

    /**
     * Fetch icon info
     */
    private static Map<String, Object> fetchIconContents() throws IOException {
        URL obj = new URL("https://fonts.google.com/metadata/icons");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            String response = new BufferedReader(new InputStreamReader(con.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
            return new Gson().fromJson(response, new TypeToken<Map<String, Object>>() {}.getType());
        } else {
            System.out.println("Failed to download icons");
            return null;
        }
    }

}
