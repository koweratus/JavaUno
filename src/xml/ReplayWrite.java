package xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Jarvis
 */
public class ReplayWrite {
    List<String> replayLista=new ArrayList();
    String fileName="replay.xml";

    public ReplayWrite(){

    }
    public void loadPodaci(List<String> podaci){
        replayLista=podaci;
    }

    public void saveReplay() throws XMLStreamException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        Document doc = createFile();

        save(doc, fileName);
    }

    private Document createFile() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        //Korijenski element
        Element replay = doc.createElement("Replay");
        doc.appendChild(replay);
        //Kod pozivanja ove metoda prije moram pozvati loadPodaci
        List<String> podaci=replayLista;

        for (String i : podaci) {
            Element replayElement=newReplayElement(doc,i);
            replay.appendChild(replayElement);
        }

        return doc;
    }
    private Element newReplayElement(Document doc, String i) {
        Element potez=doc.createElement("Potez");
        Element kolona=doc.createElement("Kolona");
        kolona.appendChild(doc.createTextNode(i));
        potez.appendChild(kolona);

        return potez;
    }

    private void save(Document doc, String filePath) throws TransformerConfigurationException, TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));

        transformer.transform(source, result);
        transformer.transform(source, new StreamResult(System.out));
    }




}
