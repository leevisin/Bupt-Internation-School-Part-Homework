import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Title	: NetAnalyser.java
 * Description	: A graphical user interface (GUI) application to execute the fundamental network diagnostic tool ping and display its raw output together with a histogram of Round Trip Time (RTT) values.
 * Copyright	: Copyright (c) 2006-2020
 * @author	: Dongsheng Li
 * @version	: 3.0
 */

public class NetAnalyser extends JFrame implements ActionListener{

  /** The processBtn is the Process Button. */
  JButton processBtn = new JButton("Process");
  /** The textField is to get the user's input url string. */
  JTextField textField = new JTextField(20);
  /** The numProbesComboxBox is to get the user input value of probes. */
  JComboBox<String> numProbesComboBox;
  /** The outputPanel is to display the raw output from the ping command. */
  JPanel outputPanel = new JPanel();
  /** The histogramPanel is to  display a histogram of the RTT values. */
  JPanel histogramPanel = new JPanel(new BorderLayout());
  /** The contents is to get the information of raw output from the ping command. */
  String contents = "Your output will appear here...";
  /** The textArea is to display the contents. */
  JTextArea textArea = new JTextArea(1,20);
  /** The hisgramLabel contains the string Histogram. */
  JLabel histogramLabel = new JLabel("Histogram                                                                               ");
  /** The numProbes is the number of probes. */
  int numProbes;
  /** The timedOutNum is the number of request time out line. */
  int timedOutNum=0;

  public NetAnalyser(int maxNumProbes){ // maxNumProbes is the maximum value of probe number.

    // Set the maximum value of probes.
    String[] listData = new String[maxNumProbes];
    for(int i=0; i<maxNumProbes; i++){
      listData[i] = ""+(i+1);
    }
    numProbesComboBox = new JComboBox<String>(listData);

    // Generate basic JLabel and inputPanel.
    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new BorderLayout());
    JLabel label1 = new JLabel("Enter Test URL & no. of probes and click on Process");
    JLabel label2 = new JLabel("Test URL");
    JLabel label3 = new JLabel("No. of probes");

    // Set processBtn size and add actionListener on it.
    processBtn.setSize(50,50);
    processBtn.addActionListener(this);

    // Set the inputPanel Layout.
    JPanel topPanel = new JPanel();
    JPanel midPanel = new JPanel();
    JPanel btmPanel = new JPanel();
    JPanel btnPanel = new JPanel();

    topPanel.add(label1);
    midPanel.add(label2);
    midPanel.add(textField);
    btmPanel.add(label3);
    btmPanel.add(numProbesComboBox);
    btnPanel.add(processBtn);
    inputPanel.add(addJPanel(addJPanel(addJPanel(topPanel,midPanel),btmPanel),btnPanel));


    // Add contents to textArea
    // Add textArea to outputPanel
    textArea.setLineWrap(true); // Word wrap
    textArea.setText(contents);
    outputPanel.add(textArea);

    // Add histogramLabel to histogramPanel
    JPanel histogramLabelPanel = new JPanel();
    histogramLabelPanel.add(histogramLabel);
    histogramPanel.add("North",histogramLabelPanel);

    // Add inputPanel, outputPanel and histogramPanel to frame.
    getContentPane().add(inputPanel,BorderLayout.WEST);
    getContentPane().add(outputPanel,BorderLayout.CENTER);
    getContentPane().add(histogramPanel,BorderLayout.EAST);
  }

  /** This method will be run when the processBtn is clicked.
   *  @param e When processBtn is clicked.
   */
  public void actionPerformed(ActionEvent e){

    // Check if the input URL is valid.
    if(!verifyUrl(textField.getText())){
      System.out.println("The input url is invalid, now we exit..");
      System.exit(1);
    }

    // numProbes get the number of probes.
    numProbes = numProbesComboBox.getSelectedIndex()+1;

    try{
      // Get the raw ouput from the ping command to reader.
      Process p = Runtime.getRuntime().exec("cmd /c chcp 437 & ping -n " + numProbesComboBox.getSelectedItem() + " " + textField.getText());
      p.waitFor();  
      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

      // Jump the command chcp 437.
      String oneLine = reader.readLine(); // oneLine = Active code page: 437

      oneLine = reader.readLine(); // When the server can be reached, oneLine = "null".
      if(oneLine.contains("Ping request could not find")){ // if the server cannot be reached, exit program.
        System.out.println("The server cannot be reached, now we exit..");
        System.exit(1);
      }
      oneLine = reader.readLine(); // oneLine = Pinging...

      // Put the information from reader to contents.
      contents = "";
      while(oneLine != null){
        if(oneLine.contains("Request timed out."))
          timedOutNum++; // Count the number of request timeout lines.
        contents += oneLine + "\n";
        oneLine = reader.readLine();
      }
      reader.close();

      // When all probes request timed out, exit program.
      if(numProbes==timedOutNum){
        System.out.println("All probes request timed out, now we exit..");
        System.exit(1);
      }

    }
    catch (IOException ex) {
            ex.printStackTrace();
        }
    catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    textArea.setText(contents); // Display raw output. 

    // The replyLine contains contents that without Request time out line.
    String replyLine = "";

    try{
      BufferedReader replyLineReader = new BufferedReader(new StringReader(contents));

      // One line of contents and jump the first line of contents.
      String replyOneLine = replyLineReader.readLine();

      // Add contents that without Request time out line to replyLine and count request timeout lines.
      for(int i=0; i<numProbes; i++){
        replyOneLine = replyLineReader.readLine(); // Get one line from contents.
        if(replyOneLine.contains("Request timed out.")){ // Check if Request time out.
          continue; 
        }
        replyLine += replyOneLine + "\n"; // Add contents that without Request time out line to replyLine.
      }
    }
    catch(IOException ex){  ex.printStackTrace(); }

    // Using splitString method to get RTT into numArray.
    String timeString = splitString(replyLine, " ", 4, 5); // The timeString contains string like time=8ms.
    String numTimeString = splitString(timeString, "time=", 1, 1); // The numTimeString contains string like 8ms.
    int[] numArray = splitStringToNum(numTimeString, "ms", 0, 1); // The numArray contains each RTT.

    // Processing histogram data
    textArea.setColumns(40);
    int maxRTT = numMax(numArray); // The maximum RTT in numArray.
    int minRTT = numMin(numArray); // The mininum RTT in numArray.
    double binSize = ((double)maxRTT - (double)minRTT)/3; // Get accurate binSize to set the size as 3.

    double firstLineNum = minRTT; // The first bin left border.
    double secondLineNum = minRTT + binSize; // The second bin left border.
    double thirdLineNum = minRTT + binSize + binSize; // The third bin left border.

    // Get the number in bin range.
    int firstLineNumInRange = numInRange(firstLineNum,firstLineNum+binSize,numArray);
    int secondLineNumInRange = numInRange(secondLineNum,secondLineNum+binSize,numArray);
    int thirdLineNumInRange = numInRangeAll(thirdLineNum,maxRTT,numArray);

    // reading now time to nowTimeString.
    Date dNow = new Date();
    SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
    String nowTimeString = ft.format(dNow);

    // Change ' ' and ':' to '-' in nowTimeString.
    nowTimeString = changeString(nowTimeString, ' ', '-');
    nowTimeString = changeString(nowTimeString, ':', '-');

    // Get url string.
    String netLinkString = textField.getText();
    netLinkString = changeString(netLinkString, '.', '-');

    String firstLineContents = netLinkString + "-" + nowTimeString + ".txt"; // The fileName.


    // When RTT are all the same value, set histogramPanel.
    if(binSize == 0){
      JLabel rttLabel = new JLabel("RTT = " + minRTT);

      JPanel thirdLineMarkerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      for(int i=0; i<thirdLineNumInRange; i++){
        thirdLineMarkerPanel.add(new JLabel("X"));
        thirdLineMarkerPanel.add(new JLabel("   "));
      }
      JPanel lastLinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      lastLinePanel.add(rttLabel);
      lastLinePanel.add(thirdLineMarkerPanel);
    
      JPanel histogramLabelPanel = new JPanel(new BorderLayout());
      histogramLabelPanel.add("North",new JPanel());
      histogramLabelPanel.add("Center",histogramLabel);
      histogramLabelPanel.add("South",new JPanel());

      histogramPanel.removeAll(); // Clear histogramPanel.

      // Add Panels to histogramPanel.
      histogramPanel.add("Center",histogramLabelPanel);
      histogramPanel.add("South",lastLinePanel);

      String lineHistogram = new String("RTT = " + minRTT + ":" + thirdLineNumInRange); // The contents of file.
      String wholeStringToWrite = firstLineContents + "\n\n" + "RTT(ms) histogram" + "\n" + lineHistogram + "\n"; // The whole contents of file.
      writeStringToFile(wholeStringToWrite, firstLineContents); // Write to file.

    }

    // When RTT are not the same value, set histogramPanel.
    else{
      JLabel firstLineLabel = new JLabel(firstLineNum + "<=RTT<" + String.format("%.1f",(firstLineNum+binSize)));
      JLabel secondLineLabel = new JLabel(String.format("%.1f",secondLineNum) + "<=RTT<" + String.format("%.1f",(secondLineNum+binSize)));
      JLabel thirdLineLabel = new JLabel(String.format("%.1f",thirdLineNum) + "<=RTT<=" + String.format("%.1f",(double)maxRTT));

      JPanel firstLineRTTPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JPanel secondLineRTTPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JPanel thirdLineRTTPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

      firstLineRTTPanel.add(firstLineLabel);
      secondLineRTTPanel.add(secondLineLabel);
      thirdLineRTTPanel.add(thirdLineLabel);

      // Add markers for markerPanel.
      JPanel firstLineMarkerPanel = panelAddMarker(firstLineNumInRange);
      JPanel secondLineMarkerPanel = panelAddMarker(secondLineNumInRange);
      JPanel thirdLineMarkerPanel = panelAddMarker(thirdLineNumInRange);

      JPanel histogramLabelPanel = new JPanel(new BorderLayout());
      histogramLabelPanel.add("North",new JPanel());
      histogramLabelPanel.add("Center",histogramLabel);
      histogramLabelPanel.add("South",new JPanel());

      // Add left and right panel to belowPanel.
      JPanel belowPanel = new JPanel(new BorderLayout());
      belowPanel.add("West",addJPanelLast(addJPanelLast(firstLineRTTPanel,secondLineRTTPanel),thirdLineRTTPanel));
      belowPanel.add("Center",addJPanelLast(addJPanelLast(firstLineMarkerPanel,secondLineMarkerPanel),thirdLineMarkerPanel));

      JPanel belowAddBlankLinePanel = new JPanel(new BorderLayout());
      belowAddBlankLinePanel.add("Center",belowPanel);
      belowAddBlankLinePanel.add("South",new JPanel());

      histogramPanel.removeAll(); // Clear histogramPanel.
    
      // Add Panels to histogramPanel.
      histogramPanel.add("Center",histogramLabelPanel);
      histogramPanel.add("South",belowAddBlankLinePanel);

      String firstLineHistogram = new String(firstLineNum + "-" + String.format("%.1f", firstLineNum+binSize) + ":" + firstLineNumInRange);
      String secondLineHistogram = new String(String.format("%.1f",secondLineNum) + "-" + String.format("%.1f", secondLineNum+binSize) + ":" + secondLineNumInRange);
      String thirdLineHistogram = new String(String.format("%.1f",thirdLineNum) + "-" + String.format("%.1f",(double)maxRTT) + ":" + thirdLineNumInRange);

      String wholeStringToWrite = firstLineContents + "\n\n" + "RTT(ms) histogram" + "\n" + firstLineHistogram + "\n" + secondLineHistogram + "\n" + thirdLineHistogram + "\n"; // The whole contents of file.
      writeStringToFile(wholeStringToWrite, firstLineContents); // Write to file.

    }

  }

  /** This method is to split and return needed string.
   *  @param str Raw string needs to be splitted.
   *  @param splitStr The string for splitting strings.
   *  @param firstShow The first needed string number in splittedStr.
   *  @param gap The gap number for needed string.
   *  @return All needed string in a string.
   */
  public String splitString(String str, String splitStr, int firstShow, int gap){
    String splittedString = ""; // The return string.
    String[] splittedStr = str.split(splitStr); // Store splitted string.
    for(int i=firstShow; i<splittedStr.length; i+=gap){
      splittedString += splittedStr[i]; // Add needed string to splittedString.
    }
    return splittedString;
  }

  /** This method is to split string and return needed num.
   *  @param str Raw string needs to be splitted.
   *  @param splitStr The string for splitting strings.
   *  @param firstShow The first needed string number in splittedStr.
   *  @param gap The gap number for needed number.
   *  @return All needed number in a string.
   */
  public int[] splitStringToNum(String str, String splitStr, int firstShow, int gap){
    int[] num = new int[numProbes-timedOutNum]; // Set array size as the number of reply lines.
    String[] splittedStr = str.split(splitStr); // Store splitted string.
    int j=0; // The operationg number for the array.
    for(int i=firstShow; i<splittedStr.length; i+=gap){
      num[j] += Integer.parseInt(splittedStr[i]); // Add needed value to array.
      j++; // Operate for next value of array.
    }
    return num;
  }

  /** This method is to get maximum value from array.
   *  @param Array The array needs to select maximum value.
   *  @return The maxinum value in the array.
   */
  public int numMax(int[] Array){
    int maxNum = Array[0];
    for(int i=0; i<Array.length; i++){
      if(Array[i]>maxNum)
        maxNum = Array[i];
    }
    return maxNum;
  }

  /** This method is to get minimum value from array.
   *  @param Array The array needs to select minimum value.
   *  @return The mininum value in the array.
   */
  public int numMin(int[] Array){
    int minNum = Array[0];
    for(int i=0; i<Array.length; i++){
      if(Array[i]<minNum)
        minNum = Array[i];
    }
    return minNum;
  }

  /** This method is to get number that are in array range, not including the maximum value.
   *  @param min Minimum value in the range.
   *  @param max Maximum value in the range.
   *  @param Array The array needs to get number that are in array range.
   *  @return The number that are in array range.
   */
  public int numInRange(double min, double max, int[] Array){
    int num=0;
    for(int i=0; i<Array.length; i++){
      if(Array[i]>=min && Array[i]<max)
        num++;
    }
    return num;
  }

  /** This method is to get number that are all in array range, including the maximum value.
   *  @param min Minimum value in the range.
   *  @param max Maximum value in the range.
   *  @param Array The array needs to get number that are in array range.
   *  @return The number that are in array range.
   */
  public int numInRangeAll(double min, double max, int[] Array){
    int num=0;
    for(int i=0; i<Array.length; i++){
      if(Array[i]>=min && Array[i]<=max)
        num++;
    }
    return num;
  }

  /** Add blank panel between first panel and second panel by using BorderLayout.
   *  Input panel in North, blank panel in Center. Both in North, second panel in Center.
   *  @param fJPanel The panel should be put in North.
   *  @param sJPanel The panel should be put in Center.
   *  @return The whole panel(BorderLayout) contains first panel and second panel.
   */
  public JPanel addJPanel(JPanel fJPanel, JPanel sJPanel){
    JPanel wholePanel = new JPanel(new BorderLayout());
    wholePanel.add("North", addBlankJPanel(fJPanel));
    wholePanel.add("Center", sJPanel);
    return wholePanel;
  }

  /** Add blank panel after the input panel by using BorderLayout.
   *  Input panel in North, blank panel in center.
   *  @param jp The Panel should be put in North.
   *  @return The whole Panel(BorderLayout) contains first panel and blank panel.
   */
  public JPanel addBlankJPanel(JPanel jp){
    JPanel wholePanel = new JPanel(new BorderLayout());
    wholePanel.add("North",jp);
    wholePanel.add("Center",new JPanel());
    return wholePanel;
  }

  /** Add marker in panel by loop.
   *  @param num The loop times.
   *  @return The whole panel(BorderLayout) contains markers label.
   */
  public JPanel panelAddMarker(int num){
    JPanel markerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    for(int i=0; i<num; i++){
      markerPanel.add(new JLabel("X"));
      markerPanel.add(new JLabel("   "));
    }
    return markerPanel;
  }

  /** Add blank panel between first panel and second panel by using BorderLayout.
   *  Input panel in North, blank panel in Center, second panel in South.
   *  @param fJPanel The panel should be put in North.
   *  @param sJPanel The panel should be put in South.
   *  @return The whole panel(BorderLayout) contains first panel and second panel.
   */
  public JPanel addJPanelLast(JPanel fJPanel, JPanel sJPanel){
    JPanel wholePanel = new JPanel(new BorderLayout());
    wholePanel.add("North", fJPanel);
    wholePanel.add("Center", new JPanel());
    wholePanel.add("South", sJPanel);
    return wholePanel;
  }

  /** Write string to file.
   *  @param str The contents of file.
   *  @param fileName The file to write.
   */
  public void writeStringToFile(String str, String fileName){
    try{
      FileWriter fileWriter = new FileWriter(fileName, true); // It can write at the end of file.
      BufferedWriter writer = new BufferedWriter(fileWriter);
      writer.write(str);
      writer.close();
      fileWriter.close();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Change a specific character of a string to another character.
   *  @param str The input string.
   *  @param ch The specific character of a string.
   *  @param desCh The final character you want.
   *  @return The changed character.
   */
  public String changeString(String str, char ch, char desCh){
    StringBuilder strBuilder = new StringBuilder(str);
      for(int i=0; i<str.length(); i++){
      if(str.charAt(i)==ch){
        strBuilder.setCharAt(i,desCh);
      }
    }
    return strBuilder.toString();
  }

  /** Checking whether url is non-empty and contains at least one dot. 
   *  @param webUrl The url string.
   *  @return if it is valid.
   */
  public boolean verifyUrl(String webUrl){
    if(webUrl==null){ // if webUrl is empty.
      return false;
    }
    // Determine whether it contains at least one dot.
    else{
      for(int i=0; i<webUrl.length(); i++){
        if(webUrl.charAt(i)=='.') // if url contains at least ont dot.
          return true;
      }
      return false; // if url does not contain any dot.
    }
  }

  public static void main(String[] args){

     int maxNum = 10; // The maximum number of probes.
     if(args.length==0){ // if the input is empty
       System.out.println("The input should be a interger at 10-20");
       System.exit(0);
     }
     else{ // if the input is not empty.
       for(int i=0; i<args[0].length(); i++){ // Check if it is a digital.
         if(!Character.isDigit(args[0].charAt(i))){ // If one of characters of args[0] is not a digital.
           System.out.println("The input should be a integer at 10-20");
           System.exit(0);
         }
       }
       maxNum = Integer.parseInt(args[0]); // Transform args[0] into integer.
       if(maxNum<10 || maxNum >20){ // if maxNum is not between 10-20.
         System.out.println("The input should be a integer at 10-20");
         System.exit(0);
       }
     }

     NetAnalyser frame = new NetAnalyser(maxNum);
     frame.setTitle("NetAnalyser V3.0");
     frame.pack();
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.setSize(1100,280);
     frame.setVisible(true);

  }

}