package xmltime;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLTIME extends JFrame implements ActionListener{
	
	private JFrame frame;
	private JButton open;
	private JButton openXml;
	
	private JButton launch;
    private String date;
	private JLabel txtpathXml;

	static String pathXml;

	
public XMLTIME() {
	frame = new JFrame("XML Parser");
	
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(400, 200);
	frame.setVisible(true);
	frame.setLayout(null);

	openXml = new JButton("Select XML/R2");
	openXml.setBounds(10,10,250,35);
	txtpathXml = new JLabel("XML/R2 Path is not selected");
	txtpathXml.setBounds(10,50,250,35);
	
	launch = new JButton("Insert Date");
	launch.setBounds(10,100,250,35);
	
	frame.add(openXml);
	
	frame.add(launch);
	frame.add(txtpathXml);

	openXml.addActionListener(this);
	launch.addActionListener(this);
}
public void changeHeader() {
	
	Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    
    File dir = new File(pathXml);
   	File[] directoryListing = dir.listFiles();
	int count = 0;
	
	if (directoryListing != null) {
		for (File child : directoryListing) {
			
			count++;
			
			try {
				
				String filename = child.getName();
				int index = filename.indexOf(".");
				String f = filename.substring(0, index);
		
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(child);
	
			    NodeList msgheaderList = doc.getElementsByTagName("ichicsrmessageheader");
			    for(int i = 0; i<msgheaderList.getLength(); i++) {
			    	Node r = msgheaderList.item(i);
			        	if(r.getNodeType()==Node.ELEMENT_NODE) {
			        		Element patient = (Element) r;
			                NodeList patientEpisode = patient.getChildNodes();
			                for(int j=0; j<patientEpisode.getLength();j++) {
			                	Node n = patientEpisode.item(j);
			                    if(n.getNodeType()==Node.ELEMENT_NODE) {
			                    	Element pat = (Element)n;
			                        String str = pat.getTextContent();
			                        
			                        if(pat.getTagName()=="messagenumb") {
			                     
			                            pat.setTextContent(str+"_"+count);
			                        }
			                            
			                        if(pat.getTagName()=="messagedate") {
			                        
			                            pat.setTextContent(date+sdf.format(cal.getTime()));
			                        }
			                            
			                    }
			                }
			            }
			    }

			    
			    DOMSource domSource = new DOMSource(doc);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                OutputStreamWriter out = null;
                if(doc.getXmlEncoding().equalsIgnoreCase("UTF-16")) {
                	out = new OutputStreamWriter(new FileOutputStream(pathXml+"\\"+f+"_"+count+".xml"),"UTF-16");
                }else if(doc.getXmlEncoding().equalsIgnoreCase("UTF-8")) {
                	out = new OutputStreamWriter(new FileOutputStream(pathXml+"\\"+f+"_"+count+".xml"),"UTF-8");
                }
                transformer.transform(domSource,new StreamResult(out));
                out.close();
                child.delete(); 
				} catch (ParserConfigurationException e) {
					JOptionPane.showMessageDialog(frame, "ParserConfigurationException");
					e.printStackTrace();
				}catch (SAXException e) {
					JOptionPane.showMessageDialog(frame, "Folder should contain XML format");
					e.printStackTrace();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(frame, "IOException");
					e.printStackTrace();
				} catch (TransformerConfigurationException e) {
					JOptionPane.showMessageDialog(frame, "TransformerConfigurationException");
					e.printStackTrace();
				} catch (TransformerException e) {
					JOptionPane.showMessageDialog(frame, "TransformerException");
					e.printStackTrace();
				}
			
			}
   	}	
}

	public static void main(String[] args) {
    	new XMLTIME();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == launch ) {
			if(pathXml != null) { 
			date = JOptionPane.showInputDialog("Please insert the date");
				if(date.length() == 8 ) {
					int input = JOptionPane.showOptionDialog(null, "File converted", "The title", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
					if(input == JOptionPane.OK_OPTION)
					{
						changeHeader();
					}
				}else {
					JOptionPane.showMessageDialog(frame, "Please insert correct date format");
				}
			}else {
				JOptionPane.showMessageDialog(frame, "Please Select Input Folder");
		}
	}
		if(e.getSource() == openXml ) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Select source folder");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if(chooser.showOpenDialog(open)==JFileChooser.APPROVE_OPTION) {
				pathXml = chooser.getSelectedFile().getAbsolutePath();
				txtpathXml.setText(pathXml);
				
			}		
		}
	}
}
