package org.davidjhartman;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmind.core.Core;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.internal.zip.ZipStreamOutputTarget;
import org.xml.sax.SAXException;
import org.davidjhartman.InformaticaMapping;



public class GenerateInformaticaMappingXMind {
	 //public static final String xmlFile="X:\\BI&A Technical\\Templates\\Informatica\\m_IU_T2_TYPE2_TEMPLATE.XML";
	 public static final String xmlFile="XML_Files\\workflow.xml";
	 
	 private static final String outXmindFileName = "Z:\\Users\\dhartman\\Dropbox\\infa.xmind";
	 public static Node repositoryNode;
	 public static String repositoryName;
	 public static List<String> folderNames = new ArrayList<String>();
	 public static List<Node> folderNodeList = new ArrayList<Node>();
	 public static List<Node> folderObjects = new ArrayList<Node>();
	 public static IWorkbook workbook;
	 
	 public static void setFolderNodeList(Node inRepositoryNode) {
		 for (int i = 0; i < inRepositoryNode.getChildNodes().getLength(); i++) {
             Node node = inRepositoryNode.getChildNodes().item(i);
             //System.out.println("Looking at node with name: " + node.getNodeName());
             if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName() == "FOLDER") {
               System.out.println("Adding folder: " + node.getAttributes().getNamedItem("NAME").getNodeValue() + " to Folder List");
               folderNodeList.add(node);
               folderNames.add(node.getAttributes().getNamedItem("NAME").getNodeValue());
             }
         } // end of for loop on nodes
	 }
	 public static void setRepositoryNode(NodeList inNodeList) {
		 //there should be only one main repository node
		 if (inNodeList.getLength() == 3 && inNodeList.item(1).getNodeType() == Node.ELEMENT_NODE && inNodeList.item(1).getNodeName() == "REPOSITORY") {
			 repositoryNode=inNodeList.item(1);
			 System.out.println("Found repository entry: " + repositoryNode.getAttributes().getNamedItem("NAME").getNodeValue() );
			 repositoryName = repositoryNode.getAttributes().getNamedItem("NAME").getNodeValue();
			 setFolderNodeList(repositoryNode);
		 } else {
			 System.out.println("Error in finding Repository Node"); 
		 }
//		 for (int i = 0; i < inNodeList.getLength(); i++) {
//             Node node = inNodeList.item(i);
//             if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName() == "REPOSITORY") {
//               repositoryNode=inNodeList.item(i);
//               setFolderNodeList(repositoryNode);
//               //Element elem = (Element) node;
//               //infaMap.setRepositoryName(node.getAttributes().getNamedItem("NAME").getNodeValue());
//             }
//         } // end of for loop on nodes
	 }
	 public static void createXmindFile() throws Exception {
			workbook = Core.getWorkbookBuilder().createWorkbook();
	        ITopic root = workbook.getPrimarySheet().getRootTopic();
	        root.setTitleText(repositoryName);
	        addFolders();
	       // addFolderObjects();
	        FileOutputStream fos = new FileOutputStream(outXmindFileName);
	        BufferedOutputStream bos = new BufferedOutputStream(fos);
	        try {
	           workbook.save(new ZipStreamOutputTarget(
	           new ZipOutputStream(bos),true));
	        } finally {
	            bos.close();
	            System.out.println("Saved workbook: " + outXmindFileName);
	        }
	 }
	 public static void addFolders() {
		 ITopic root = workbook.getPrimarySheet().getRootTopic();
		 for (String fldrName : folderNames) {
	        	ITopic curTopic = workbook.createTopic();
	        	curTopic.setTitleText(fldrName);
	        	addFolderObjects(curTopic);
	        	root.add(curTopic);
	     }
	 }
	 public static void addFolderObjects(ITopic inFolderTopic) {
		 //ITopic root = workbook.getPrimarySheet().getRootTopic();
		 //go through each folder node
		 for (Node fldrNode : folderNodeList) {
			 // go through each object in the folder
			 ITopic folderRootNode = workbook.createTopic();
			 for (int j = 0; j < fldrNode.getChildNodes().getLength(); j++) {
				Node node = fldrNode.getChildNodes().item(j);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					String objectName = node.getAttributes().getNamedItem("NAME").getNodeValue();
					String objectType = node.getNodeName() ;
		        	ITopic curTopic = workbook.createTopic();
		        	curTopic.setTitleText(objectName);
		        	curTopic.addLabel(objectType);
		        	folderRootNode.add(curTopic);
				}
			 }
			 inFolderTopic.add(folderRootNode);
	     }
	 }
	 public static void main(String[] args)  throws Exception {
	 //ParserConfigurationException,SAXException, IOException {
//		 if(args.length != 1)
//             throw new RuntimeException("The name of the XML file is required!");
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();

         // Load the input XML document, parse it and return an instance of the
         // Document class.
         Document document = builder.parse(new File(xmlFile));
         //InformaticaMapping infaMap = new InformaticaMapping();
         NodeList nodeList = document.getDocumentElement().getChildNodes();
         setRepositoryNode(nodeList);
         //repository node list and folder node lists are setup
         createXmindFile();
	 }
	 
	 
}
