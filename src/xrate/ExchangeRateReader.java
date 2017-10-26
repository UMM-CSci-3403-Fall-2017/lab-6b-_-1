package xrate;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

/**
 * Provide access to basic currency exchange rate services.
 * 
 * @author _
 */
public class ExchangeRateReader {
    String baseURL;
    /**
     * Construct an exchange rate reader using the given base URL. All requests
     * will then be relative to that URL. If, for example, your source is Xavier
     * Finance, the base URL is http://api.finance.xaviermedia.com/api/ Rates
     * for specific days will be constructed from that URL by appending the
     * year, month, and day; the URL for 25 June 2010, for example, would be
     * http://api.finance.xaviermedia.com/api/2010/06/25.xml
     * 
     * @param baseURL
     *            the base URL for requests
     */
    public ExchangeRateReader(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * Get the exchange rate for the specified currency against the base
     * currency (the Euro) on the specified date.
     * 
     * @param currencyCode
     *            the currency code for the desired currency
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public float getExchangeRate(String currencyCode, int year, int month, int day)
            throws IOException, ParserConfigurationException, SAXException {
        String dayS = (day+"");
        String monthS = month +"";
        if (day < 10){
            dayS = "0"+ day;
        }
        if (month < 10){
            monthS = "0"+ month;
        }

        String reqString = baseURL + year + "/" + monthS + "/" + dayS + ".xml";
        String xml = "";
        float er = -1;

        URL reqURL = new URL(reqString);
        URLConnection xc = reqURL.openConnection();
        InputStream xmlStream = reqURL.openStream();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(xmlStream);

        doc.getDocumentElement().normalize();
        NodeList nl = doc.getElementsByTagName("fx");

        for(int i = 0; i < nl.getLength(); i++){
            Node node = nl.item(i);
            NodeList nodes = node.getChildNodes();
            Node exCode = nodes.item(1);

            if(currencyCode.equals(exCode.getTextContent())){
                Node exRate = nodes.item(3);
                er = new Float(exRate.getTextContent());
                break;
            }
        }
        return er;
    }

    /**
     * Get the exchange rate of the first specified currency against the second
     * on the specified date.
     * 
     * @param fromCurrency
     *            the currency code for the currency to go from
     * @param toCurrency
     *            the currency code for the currency to go to
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public float getExchangeRate(String fromCurrency, String toCurrency, int year, int month, int day)
            throws ParserConfigurationException, SAXException, IOException {
        float from = this.getExchangeRate(fromCurrency,year,month,day);
        float to = this.getExchangeRate(toCurrency,year,month,day);
        return from/to;
    }
}