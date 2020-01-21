package xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Controller;
import application.SettingsController;
import javafx.application.Platform;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import logic.Game;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class ReplayRead implements Runnable{
    String fileName="replay.xml";
    List<Integer> lista=new ArrayList();
    Game con;
    public Thread t;
    int i;



    public ReplayRead(List<String> listaStringova, Game Con){
        t=new Thread(this);
        t.setDaemon(true);
        con=Con;
        t.start();
    }

    private void readXML() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=factory.newDocumentBuilder();
        Document doc=builder.parse(fileName);

        NodeList potezList=doc.getElementsByTagName("Potez");
        for (int j = 0; j < potezList.getLength(); j++) {
            Node p=potezList.item(j);
            if (p.getNodeType()==Node.ELEMENT_NODE) {
                Element potez=(Element) p;
                NodeList kolona=potez.getChildNodes();
                for (int k = 0; k < kolona.getLength(); k++) {
                    Node kolonaNode=kolona.item(k);
                    if (kolonaNode.getNodeType()==Node.ELEMENT_NODE) {
                        Element kolonaElement=(Element) kolonaNode;
                        System.out.println(kolonaElement.getTextContent());
                        lista.add(Integer.parseInt(kolonaElement.getTextContent()));
                    }
                }
            }
        }
    }
    @Override
    public void run() {
        try {
            readXML();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ReplayRead.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ReplayRead.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReplayRead.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
