package com.example.gkaakash;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gkaakash.controller.Report;
import com.gkaakash.controller.Startup;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class balanceSheet extends Activity{
   
    static Integer client_id;
	private Report report;
	private Object[] balancesheetresult;
	String financialFromDate,financialToDate,accountName,projectName,fromDate,toDate,balancetype;
	TableRow tr;
	private ArrayList<ArrayList<String>> BalanceSheetGrid;
	private ArrayList<String> balancesheetresultList;
	private TableLayout balanceSheetTable1; 
	private TableLayout balanceSheetTable2;
	private View label;
	private ArrayList<String> TotalAmountList;
	private TextView balDiff;
	String balanceToDateString;
	String getSelectedOrgType;
	private String balancefromDateString;
	Boolean updown=false;
	ScrollView sv;
	DecimalFormat formatter = new DecimalFormat("#,##,##,###.00");
	String colValue;
	
	
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
       
       try {
       balancetype=reportMenu.balancetype;
       
       if(balancetype.equals("Conventional Balance Sheet"))
       {
    	   setContentView(R.layout.balance_sheet_table);
    	   sv = (ScrollView)findViewById(R.id.ScrollBalanceSheet);
       }
       else
       {
    	   setContentView(R.layout.vertical_balance_sheet_table);
    	   sv = (ScrollView)findViewById(R.id.ScrollVertiBalanceSheet);
       }
       
       //customizing title bar
       getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.bank_recon_title);
      
       report = new Report();
       client_id= Startup.getClient_id();
       financialFromDate =Startup.getfinancialFromDate();
       financialToDate=Startup.getFinancialToDate();
       // balancefromDateString = reportMenu.givenfromDateString;
       balanceToDateString = reportMenu.givenToDateString;
       
       balanceSheetTable1 = (TableLayout)findViewById(R.id.col1);
   		balanceSheetTable2 = (TableLayout)findViewById(R.id.col2);
   		balDiff = (TextView) findViewById(R.id.tvdifference);
   		
       getSelectedOrgType = reportMenu.orgtype;
       /*
	     * set financial from date and to date in textview
	     */
	    TextView tvfinancialFromDate = (TextView) findViewById( R.id.tvTfinancialFromDate );
	    TextView tvfinancialToDate = (TextView) findViewById( R.id.tvTfinancialToDate );
	   
	    final TextView tvReportTitle = (TextView)findViewById(R.id.tvReportTitle);
	    tvReportTitle.setText("Menu >> "+"Report >> "+balancetype);
	    final Button btnSaveRecon = (Button)findViewById(R.id.btnSaveRecon);
	    btnSaveRecon.setVisibility(Button.GONE);
	    final Button btnScrollDown = (Button)findViewById(R.id.btnScrollDown);
	    btnScrollDown.setOnClickListener(new OnClickListener() {
			
		@Override
		public void onClick(View v) {
			if(updown==false){
					sv.fullScroll(ScrollView.FOCUS_DOWN); 
	               btnScrollDown.setBackgroundResource(R.drawable.up);
	               updown=true;
	        }else {
	        	  sv.fullScroll(ScrollView.FOCUS_UP); 
	               btnScrollDown.setBackgroundResource(R.drawable.down);
	               updown=false;
	        }
				}
	    });
	    
	   //tvfinancialFromDate.setText("Financial from : " +financialFromDate);
	   tvfinancialToDate.setText("Period : "+financialFromDate+" to "+balanceToDateString);
       Object[] params = new Object[]{financialFromDate,financialFromDate,balanceToDateString,"balancesheet",getSelectedOrgType,balancetype};
       balancesheetresult = (Object[]) report.getBalancesheetDisplay(params,client_id);
       //balancesheetresult is 3 dimensional list 
       int count = 0;
  
       
       for(Object tb : balancesheetresult){

           Object[] t = (Object[]) tb;
           count = count + 1;
           BalanceSheetGrid = new ArrayList<ArrayList<String>>();
           if(count !=3)   
           {
	           for(Object tb1 : t){
	        
	           	Object[] t1 = (Object[]) tb1;
	           
	           	balancesheetresultList = new ArrayList<String>();
	
	           	for(int j=0;j<t1.length;j++){
	           		balancesheetresultList.add((String) t1[j].toString());
	           	}	
           BalanceSheetGrid.add(balancesheetresultList);
            
           } 
           }
         
           if (count == 3)
           {
        	  
        	   final SpannableString rsSymbol = new SpannableString(balanceSheet.this.getText(R.string.Rs)); 
        	   System.out.println("diff"+t[0].toString());
        	   balDiff.setText("Difference in Opening Balances: "+rsSymbol+" "+t[0].toString());
           }
           if(count == 1){
           	addTable(balanceSheetTable1);
           }
           else if(count == 2){
           	addTable(balanceSheetTable2);
           }
           
       }
       
       
   } catch (Exception e) {
   	AlertDialog.Builder builder = new AlertDialog.Builder(balanceSheet.this);

          builder.setMessage("Please try again")
                  .setCancelable(false)
                  .setPositiveButton("Ok",
                          new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int id) {

                              }
                          });

          AlertDialog alert = builder.create();
          alert.show();		}
}
    private void addTable(TableLayout tableID) {
    		System.out.println(BalanceSheetGrid);
    	   /** Create a TableRow dynamically **/
    	   for(int i=0;i<BalanceSheetGrid.size();i++){
    	   	
    	       ArrayList<String> columnValue = new ArrayList<String>();
    	       columnValue.addAll(BalanceSheetGrid.get(i));
    	       //create new row
    	       tr = new TableRow(this);
    	      
    	       if(columnValue.get(1).equalsIgnoreCase("Amount")||columnValue.get(1).equalsIgnoreCase("Debit")){
    	       	//for heading pass green color code
    	    	  // System.out.println("iam in chaninging color "+columnValue.get(1));
    	       	setRowColorSymbolGravity(columnValue, Color.parseColor("#348017"),true);
    	       }
    	       else{
    	       	//for remaining rows pass black color code
    	       	setRowColorSymbolGravity(columnValue, Color.BLACK,false);
    	       }
    	       
    	       // Add the TableRow to the TableLayout
    	       tableID.addView(tr, new TableLayout.LayoutParams(
    	               LayoutParams.FILL_PARENT,
    	               LayoutParams.WRAP_CONTENT));
    	   }
    	 
    	}
/*
 * 1. set the green background color for heading and black for remaining rows
 * 2. set right aligned gravity for amount and center aligned for other values
 * 3. set rupee symbol for amount
 */
private void setRowColorSymbolGravity(ArrayList<String> columnValue, int color,Boolean headerFlag) {
	for(int j=0;j<columnValue.size();j++){
        /** Creating a TextView to add to the row **/
		 	
		 	if(headerFlag == true){
		 		if(j == 1 || j == 2 || j == 3){ //amount column
		 		//For adding rupee symbol
		 		final SpannableString rsSymbol = new SpannableString(balanceSheet.this.getText(R.string.Rs));
		 		addRow(rsSymbol+" "+columnValue.get(j));
		 		}else{
		 		addRow(columnValue.get(j));
		 		}

		 		}else{
		 			addRow(columnValue.get(j));
		 		}
		 		
	        label.setBackgroundColor(color);
	        String name = columnValue.get(0).toString();
	        String amount = columnValue.get(1).toString();
	        String total_amount = columnValue.get(2).toString();
	        String totalamount = columnValue.get(3);
	       
	       
	       
	        if(j==1)
	        {//for amount coloumn
	    		
	    			
	                if(amount.equalsIgnoreCase("Net Surplus")
	                		||amount.equalsIgnoreCase("Net Loss")
	                		||amount.equalsIgnoreCase("Net DEFICIT")
	                		||amount.equalsIgnoreCase("NET PROFIT")
	                		||amount.equalsIgnoreCase("Amount")
	                		||amount.equalsIgnoreCase("Debit"))
	            	{
	        			((TextView)label).setGravity(Gravity.CENTER);
	        			
	            	}else{
	            		((TextView)label).setGravity(Gravity.RIGHT);
			            //For adding rupee symbol
			            if(columnValue.get(j).length() > 0)
			            {
			            	colValue=columnValue.get(j);
			            	if(!"".equals(colValue)){
			            		System.out.println("m in ");
                        		if(!"0.00".equals(colValue)){
                        			Pattern pattern = Pattern.compile("\\n");
                        			Matcher matcher = pattern.matcher(colValue);
                        			boolean found = matcher.find();
                        			System.out.println("value:"+found);
                        			if(found==false){
                        				double amount1 = Double.parseDouble(colValue);	
                        				System.out.println("A:"+amount1);
                        				((TextView) label).setText(formatter.format(amount1));
                        			}else {
                        				((TextView) label).setText(colValue);
									}
                        		}
			            	}
			            	
			            	
			            }
	            		
	            	}
	               
	    		}
	        
	    	if(j==2){//for amount coloumn

	            if(total_amount.equalsIgnoreCase("Credit"))
	            {
	    			((TextView)label).setGravity(Gravity.CENTER);
	    			
	            }else
	            {
	            	((TextView)label).setGravity(Gravity.RIGHT);
		            //For adding rupee symbol
		            if(columnValue.get(j).length() > 0)
		            {	
		            	colValue=columnValue.get(j);
	            	if(!"".equals(colValue)){
	            		System.out.println("m in ");
                		if(!"0.00".equals(colValue)){
                			Pattern pattern = Pattern.compile("\\n");
                			Matcher matcher = pattern.matcher(colValue);
                			boolean found = matcher.find();
                			System.out.println("value:"+found);
                			if(found==false){
                				double amount1 = Double.parseDouble(colValue);	
                				System.out.println("A:"+amount1);
                				((TextView) label).setText(formatter.format(amount1));
                			}else {
                				((TextView) label).setText(colValue);
							}
                		}
	            	}
	            	
		            }
            		
	            	
	            	
	            }
	        }
	        if(j==0) {
	        	if(balancetype.equals("Conventional Balance Sheet"))
	        	{
		        	if(name.equals("CORPUS")
		        			||name.equals("CAPITAL") 
		        			||name.equals("RESERVES")
		        			||name.equals("LOANS(Liability)")
		        			||name.equals("CURRENT LIABILITIES")
		        			||name.equals("FIXED ASSETS")
		        			||name.equals("CURRENT ASSETS")
		        			||name.equals("LOANS(Asset)")
		        			||name.equals("INVESTMENTS")
		        			||name.equals("MISCELLANEOUS EXPENSES(ASSET)"))
		        	{
		        		((TextView)label).setGravity(Gravity.LEFT);
		        	}
		        	else
		        	{
		        		if(name.equalsIgnoreCase("Total"))
		        		{
		        			
		        			((TextView)label).setGravity(Gravity.RIGHT);
		        		}
		        		else{
		        			   
		        	        label.setBackgroundColor(color);
		        			((TextView)label).setGravity(Gravity.CENTER);
		        		}
		        		
		        	}
	        	}
	        	else{
	        		if(name.equalsIgnoreCase("TOTAL CORPUS")
	        				||name.equalsIgnoreCase("TOTAL CAPITAL")
		        			||name.equalsIgnoreCase("TOTAL RESERVES & SURPLUS") 
		        			||name.equalsIgnoreCase("TOTAL MISCELLANEOUS EXPENSES(ASSET)")
		        			||name.equalsIgnoreCase("TOTAL OF OWNER'S FUNDS")
		        			||name.equalsIgnoreCase("TOTAL BORROWED FUNDS")
		        			||name.equalsIgnoreCase("TOTAL FUNDS AVAILABLE / CAPITAL EMPLOYED")
		        			||name.equalsIgnoreCase("TOTAL FIXED ASSETS(NET)")
		        			||name.equalsIgnoreCase("TOTAL LONG TERM INVESTMENTS")
		        			||name.equalsIgnoreCase("TOTAL LOANS(ASSET)")
		        			||name.equalsIgnoreCase("TOTAL CURRENT ASSETS")
		        			||name.equalsIgnoreCase("TOTAL CURRENT LIABILITIES")
		        			||name.equalsIgnoreCase("NET CURRENT ASSETS OR WORKING CAPITAL")
		        			
	        				){
	        			((TextView)label).setGravity(Gravity.RIGHT);
	        		}
	        		if(name.equalsIgnoreCase("Particulars"))
	        		{
	        			((TextView)label).setGravity(Gravity.CENTER);
	        		}
	        		
	        	} 
	        }
	        if(j==3)
	        	{//for amount coloumn
	        		if(totalamount.equalsIgnoreCase("Total Amount")||totalamount.equalsIgnoreCase("Amount"))
	                {
	        			((TextView)label).setGravity(Gravity.CENTER);
	        			
	                }
	        		else
	        		{
	        			((TextView)label).setGravity(Gravity.RIGHT);
		                //For adding rupee symbol
		                if(columnValue.get(j).length() > 0){
		                
		                	colValue=columnValue.get(j);
			            	if(!"".equals(colValue)){
			            		System.out.println("m in ");
	                    		if(!"0.00".equals(colValue)){
	                    			Pattern pattern = Pattern.compile("\\n");
	                    			Matcher matcher = pattern.matcher(colValue);
	                    			boolean found = matcher.find();
	                    			System.out.println("value:"+found);
	                    			if(found==false){
	                    				double amount1 = Double.parseDouble(colValue);	
	                    				System.out.println("A:"+amount1);
	                    				((TextView) label).setText(formatter.format(amount1));
	                    			}else {
	                    				((TextView) label).setText(colValue);
									}
	                    		}
			            	}
			            	
		                }
	        	}
	           
	        }
	        if(!balancetype.equals("Conventional Balance Sheet"))
	        {
	         if(j==4)
	         {//for amount coloumn
	        	 if(columnValue.get(4).toString().equalsIgnoreCase("Amount"))
	                {
	        		 	((TextView)label).setGravity(Gravity.CENTER);
	        
	                }
	        	 else
	        	 {
	        		 ((TextView)label).setGravity(Gravity.RIGHT);
	        		 	//For adding rupee symbol
	        		 	if(columnValue.get(j).length() > 0)
	        		 	{
	        		 		colValue=columnValue.get(j);
			            	if(!"".equals(colValue)){
			            		System.out.println("m in ");
                     		if(!"0.00".equals(colValue)){
                     			Pattern pattern = Pattern.compile("\\n");
                     			Matcher matcher = pattern.matcher(colValue);
                     			boolean found = matcher.find();
                     			System.out.println("value:"+found);
                     			if(found==false){
                     				double amount1 = Double.parseDouble(colValue);	
                     				System.out.println("A:"+amount1);
                     				((TextView) label).setText(formatter.format(amount1));
                     			}else {
                     				((TextView) label).setText(colValue);
									}
                     		}
			            	}
			            	
	        		 	}
	        	 }
	           }
	        }
	    }
    }

	/*
 * this function add the value to the row
 */
void addRow(String param){
    label = new TextView(this);
    ((TextView) label).setText(param);
    ((TextView) label).setTextColor(Color.WHITE);
    label.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT));
    label.setPadding(2, 2, 2, 2);
    LinearLayout Ll = new LinearLayout(this);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT);
    params.setMargins(1, 1, 1, 1);
    Ll.addView(label,params);
    tr.addView((View)Ll); // Adding textView to tablerow.
}
   
}