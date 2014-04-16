package redditsearch;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.io.File; 
import jxl.*; 
import jxl.write.*; 
import jxl.write.Number;
import java.sql.Timestamp;
import java.io.IOException;
import jxl.write.DateTime;
import jxl.write.DateFormats;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/**
 *
 * @author Zulthura
 */
public class RedditSearch {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, WriteException {
        
//            //multi search terms    
//            String terms[] = {"NSA","Comcast","Anonymous","Time+Warner","CISPA","SOPA","TPP","Swartz",
//                "FCC","Flappy","net+neutrality","Bitcoin","GCHQ","Snowden","spying","Clapper","Congress",
//                "Obama","Feinstein","Wyden","anti-piracy","FBI","CIA","DEA","EFF","ACLU","Dogecoin","breaking"};
//
//            for(int i = 0; i < terms.length; i++) 
//            {
//                terms[i] = terms[i].replace(" ","+");
//            }
            
            //listen
            Scanner s = new Scanner(System.in);

            //get subreddit
            System.out.println("\n\nSUBREDDIT (case sensitive!)\nEnter Subreddit Name (ie: technology)");
	    String subReddit = s.next();
        
            //clean subreddit string 
            subReddit = subReddit.replace(" ","");
            subReddit = subReddit.replace("/r/","");
            subReddit = subReddit.replace("r/","");
            
            //Search Term
            System.out.println("\nSEARCH TERM (not case sensitive)\nEnter Search Term (ie: TesLa)");             
            String terms = s.next();
            
            //time frame
            int t = 6;  
            while (t > 5 || t < 0)
            {
            
                System.out.println("\nTIME FRAME (from the last...)\nEnter a digit (ie: 3): \n3: last month, 4: last year, 5: all time");             
                        
                try
                    {
                        t = s.nextInt();
                    }
                catch(Exception e)
                    {
                        System.out.println("invalid entry");  
                        s.next();
                        t = 6;
                    }
            }
            
            String timeFrame[] = {"hour","day","week","month","year","all"};
            
            
            //String terms = bufferRead.readLine();
            
            //clean up terms
            terms = terms.replace(" ","+");
            
            //open excel
		java.util.Date date= new java.util.Date();
		System.out.println(new Timestamp(date.getTime()));
		 
		//String fileName = subReddit + terms[0] + date.getTime() + ".xls"; 
		String fileName = subReddit + terms + date.getTime() + ".xls"; 
		 
		WritableWorkbook workbook = Workbook.createWorkbook(new File(fileName));		
		WritableSheet sheet1 = workbook.createSheet("First Sheet", 0);
                
                //start number label arrays
                Number[] numberArray;
	        numberArray = new Number[1];
                
	        Label[] labelArray;                
	        labelArray = new Label[1];  
                
                //label chart
                labelArray[0] = new Label(0, 0, "Search Term");
                sheet1.addCell(labelArray[0]);
                labelArray[0] = new Label(1, 0, "up");
                sheet1.addCell(labelArray[0]);	
                labelArray[0] = new Label(2, 0, "down");
                sheet1.addCell(labelArray[0]);	
                labelArray[0] = new Label(3, 0, "date");
                sheet1.addCell(labelArray[0]);	
                labelArray[0] = new Label(4, 0, "link");
                sheet1.addCell(labelArray[0]);	
          	
            //intialize html
            String html = "error1";
            //row counter
            int r = 1;  
            
            //y counter
            int y = 0;
            
        //while (y < terms.length)
        while (y < 1)
        {             
           //starting URLs
           //String url = "http://www.reddit.com/search?q=subreddit%3A" + subReddit + "+" + terms[y] + "&sort=new&restrict_sr=off&t=" + timeFrame[t];                           
           String url = "http://www.reddit.com/search?q=subreddit%3A" + subReddit + "+" + terms + "&sort=new&restrict_sr=off&t=" + timeFrame[t];                           
           String commentsLink = "http://www.reddit.com/r/" + subReddit + "/comments/";
           
           System.out.println("\nSTARTING PAGE: " + url); 
           
            html = GetHTMLString(url);            
                
            int masterCounter = html.indexOf(commentsLink);            
            //System.out.println("MC: " + masterCounter); 
            
            //if(terms[y] == "")
            if(terms == "")
            {
                masterCounter = -1;
                System.out.println("Search Term is Blank");                 
            }
                    while (masterCounter > 0)
                        {
                            //get comment links
                            int counter = html.indexOf(commentsLink);     
                            
                            while (counter > 0)
                            { 
                                String commentLink = GetNextCommentLink(html,commentsLink); 
                                html = cutLastComment(html,commentLink);
                                
                                    //odd comment fix
                                    if (commentLink.indexOf("c4gorx9") > 0)
                                    { 
                                        System.out.println("c4gorx9 Bug"); 
                                        commentLink = startStop(commentLink,"http","c4gorx9");
                                        commentLink = "http" + commentLink;
                                    }
                                
                                    //name
                                    System.out.println(" " + commentLink.substring(21)); 

                                    String htmlCom = GetHTMLString(commentLink);
                                    
                                        //up
                                        String karmaU = startStop(htmlCom,"upvotes\"><span class='number'>","</span>");
                                        karmaU = karmaU.replace(",", "");
                                        int karmaUi = Integer.parseInt(karmaU);
                                        
                                    if(karmaUi > 0)
                                    {
                                        //down
                                        String karmaD = startStop(htmlCom,"downvotes\"><span class='number'>","</span>");
                                        karmaD = karmaD.replace(",", "");
                                        int karmaDi = Integer.parseInt(karmaD);
                                        
                                        //time
                                        String date1 = startStop(htmlCom,"<time datetime=\"","T");                                        
                                        System.out.println("   d:" + date1 + " r:" + r); 
                                        //date1 = "=DATEVALUE(\"" + date1 + "\")"; //excel time fix
                                        
                                        try 
                                            { 
                                                String datestr = date1;
                                                DateFormat formatter; 
                                                Date dateF; 
                                                formatter = new SimpleDateFormat("yyyy-MM-dd");
                                                dateF = (Date)formatter.parse(datestr); 

                                                WritableCellFormat cf1 = new WritableCellFormat(DateFormats.DEFAULT);
                                                DateTime dt = new DateTime(3, r, dateF, cf1);
                                                sheet1.addCell(dt);

                                                //System.out.println(date1); 
                                            }
                                        catch(Exception e) //default to text date
                                            { 
                                                System.out.println("date failed");

                                                labelArray[0] = new Label(3, r, date1);
                                                sheet1.addCell(labelArray[0]);
                                            }                                             
                                        
                                        //labelArray[0] = new Label(0, r, terms[y]);
                                        labelArray[0] = new Label(0, r, terms);
                                        sheet1.addCell(labelArray[0]);
                                        
                                        numberArray[0] = new Number(1, r, karmaUi);
                                        sheet1.addCell(numberArray[0]);	
                                        
                                        numberArray[0] = new Number(2, r, karmaDi);
                                        sheet1.addCell(numberArray[0]);	
                                        
                                        labelArray[0] = new Label(4, r, commentLink);
                                        sheet1.addCell(labelArray[0]);
                                    
                                        r++;
                                    }
                                    
                                        counter = html.indexOf(commentsLink);                                        
                                        //System.out.println("counter: " + counter);
                                        
                                        //breaker
                                        //break;
                            }                        
                            
                            //get next page
                            url = getNextUrl(html);
                            
                            if (url == "null")
                            {                            
                                //System.out.println("That Was Last Page of " + terms[y] + " on subreddit " + subReddit + "\n" + r + " Links Found\n"); 
                                System.out.println("\nThat Was Last Page of " + terms + " on subreddit " + subReddit + "\n" + (r-1) + " Links Found\n"); 
                                //System.out.println(html); 
                                masterCounter = -1;
                                //y++;
                            }
                            else
                            {  
                                url = url.replace("&amp;", "&");

                                html = GetHTMLString(url);
                                masterCounter = html.indexOf(commentsLink);  
                                //masterCounter = 0;

                                //what's the next page?
                                System.out.println("\nNext Page: " + url + "\nmasterCounter: " + masterCounter);
                                                                
//                                //breaker
//                                masterCounter = -1;
//                                y++; 
                            }  
                        }                            
                        //next page
                        y++;
                    }
        
		//close excel
		workbook.write(); 
		workbook.close();
		
		//fin
		System.out.println("RedditSearch ran succesfully (" + (r-1) + " links found), your Excel file is finished\nPress Enter to Exit");
                System.in.read();        
        }
    
    public static String startStop(String html, String start, String stop)
    {       
        try
            {
                int left = html.indexOf(start) + start.length();
                int right = html.length();   

                html = html.substring(left,right);
                //System.out.println(html);
                html = html.substring(0,html.indexOf(stop));                      

                return html;    
            }
        catch(Exception e)
            {
               System.out.println("startStop() failed");
               return "null";   
            }
    
    }

    public static String cutLastComment(String html, String commentLink)
    {
         try
            {
                String end = "</html>";

                int cutLastComment = html.indexOf(commentLink) + commentLink.length();
                int endHtml = html.indexOf(end) + end.length();

                html = html.substring(cutLastComment,endHtml);

                return html;   
            }
        catch(Exception e)
            {
               System.out.println("cutLastComment() failed");
               return "null";   
            }
    }
    
    public static String GetNextCommentLink(String html, String commentsLink)
    {
         try
            {        
                String commentLink = commentsLink;
                String commentEnd = "/\"";

                int left = html.indexOf(commentLink);
                int right = html.length();

                html = html.substring(left,right);
                html = html.substring(0,html.indexOf(commentEnd));            

                return html;
            }
         catch(Exception e)
            {
                System.out.println("GetNextCommentLink() failed");
                return "null";               
            }
         
    }
    
    public static String getNextUrl(String html)
    {
         try
            {     
                if(html.indexOf("prev</a>") > 0) 
                    {
                        html = html.substring(html.indexOf("prev</a>"),(html.indexOf("</html>")+ "</html>".length()));
                    }

                if(html.indexOf("http://www.reddit.com/search?") > 0)
                    {
                        String commentLink = "http://www.reddit.com/search?";
                        String commentEnd = "\"";

                        int left = html.indexOf(commentLink);
                        int right = html.indexOf("</html>");

                        html = html.substring(left,right);            

                        html = html.substring(0,html.indexOf(commentEnd));            

                        return html;
                    }
                else
                    {
                        return "null";
                    }
            }
         catch(Exception e)
            {
                System.out.println("getNextUrl() failed");
                return "null"; 
            }
    }            
            
    public static String GetHTMLString(String url)
    {
         try
         {
            String content = null;
            URLConnection connection = null;
            
            try 
            {
            connection =  new URL(url).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            }catch ( Exception ex ) 
            {
                   System.out.println("ex Exception"); //idk
            }
            return content;
         }
        catch(Exception e)
            {
                System.out.println("GetHTMLString() failed");
                return "null"; 
            }
         
    }
    
}
