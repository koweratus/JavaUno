package xml;

import logic.Card;
import logic.CardType;
import logic.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlHelper {
    public static Element rootElement(Document doc) {
        Element rootEl = doc.createElement("Uno");
        doc.appendChild(rootEl);
        return rootEl;
    }

    public static Element moveElement(Document doc, Element parentElement) {
        Element moveEl = doc.createElement("Move");
        parentElement.appendChild(moveEl);

        return moveEl;
    }

    public static Element dealerElement(Document doc, Element parentElemet) {
        Element dealerEl = doc.createElement("Dealer");
        parentElemet.appendChild(dealerEl);

        return dealerEl;
    }

    public static Element playerHandElement(Document doc, Element parentElement) {
        Element dealerCards = doc.createElement("Cards");
        parentElement.appendChild(dealerCards);

        return dealerCards;
    }

    public static Element cardElement(Document doc, Element parentElement) {
        Element cardEl = doc.createElement("Card");
        parentElement.appendChild(cardEl);

        return cardEl;
    }

    public static void cardColorElement(Document doc, Element parentElement, Color color) {
        Element cardSuit = doc.createElement("Suit");
        cardSuit.appendChild(doc.createTextNode(color.toString()));
        parentElement.appendChild(cardSuit);
    }

    public static void cardNumberElement(Document doc, Element parentElement, CardType number) {
        Element cardVal = doc.createElement("Value");
        cardVal.appendChild(doc.createTextNode(number.toString()));
        parentElement.appendChild(cardVal);
    }
    public static void cardValueElement(Document doc, Element parentElement, int value) {
        Element cardVal = doc.createElement("Value");
        cardVal.appendChild(doc.createTextNode(Integer.toString(value)));
                parentElement.appendChild(cardVal);
    }

    public static Card cardFromString(String color, String number, String value) {
        Card dummyCard = new Card();

        switch (color) {
            case "ALL":
                dummyCard.setColor(Color.ALL);
                break;
            case "BLACK":
                dummyCard.setColor(Color.BLACK);
                break;
            case "RED":
                dummyCard.setColor(Color.RED);
                break;
            case "BLUE":
                dummyCard.setColor(Color.BLUE);
                break;
            case "YELLOW":
                dummyCard.setColor(Color.YELLOW);
                break;
            case "GREEN":
                //System.out.println("test spades");
                dummyCard.setColor(Color.GREEN);
                break;
        }
        switch (number) {
            case "ZERO":
                dummyCard.setType(CardType.ZERO);
                break;
            case "ONE":
                dummyCard.setType(CardType.ONE);
                break;
            case "TWO":
                dummyCard.setType(CardType.TWO);
                break;
            case "THREE":
                dummyCard.setType(CardType.THREE);
                break;
            case "FOUR":
                dummyCard.setType(CardType.FOUR);
                break;
            case "FIVE":
                dummyCard.setType(CardType.FIVE);
                break;
            case "SIX":
                dummyCard.setType(CardType.SIX);
                break;
            case "SEVEN":
                dummyCard.setType(CardType.SEVEN);
                break;
            case "EIGHT":
                dummyCard.setType(CardType.EIGHT);
                break;
            case "NINE":
                dummyCard.setType(CardType.NINE);
                break;
            case "REVERSE":
                dummyCard.setType(CardType.REVERSE);
                break;
            case "SKIP":
                dummyCard.setType(CardType.SKIP);
                break;
            case "DRAW_TWO":
                dummyCard.setType(CardType.DRAW_TWO);
                break;
            case "DRAW_FOUR":
                dummyCard.setType(CardType.DRAW_FOUR);
                break;
            case "WILD":
                dummyCard.setType(CardType.WILD);
                break;


        }
        switch (value) {
            case "ZERO":
                dummyCard.setValue(0);
                break;
            case "ONE":
                dummyCard.setValue(0);
                break;
            case "TWO":
                dummyCard.setValue(0);
                break;
            case "THREE":
                dummyCard.setValue(0);
                break;
            case "FOUR":
                dummyCard.setValue(0);
                break;
            case "FIVE":
                dummyCard.setValue(0);
                break;
            case "SIX":
                dummyCard.setValue(0);
                break;
            case "SEVEN":
                dummyCard.setValue(0);
                break;
            case "EIGHT":
                dummyCard.setValue(0);
                break;
            case "NINE":
                dummyCard.setValue(0);
                break;
            case "REVERSE":
                dummyCard.setValue(1);
                break;
            case "SKIP":
                dummyCard.setValue(1);
                break;
            case "DRAW_TWO":
                dummyCard.setValue(2);
                break;
            case "DRAW_FOUR":
                dummyCard.setValue(10);
                break;
            case "WILD":
                dummyCard.setValue(5);
                break;


        }
        return dummyCard;
    }
}
