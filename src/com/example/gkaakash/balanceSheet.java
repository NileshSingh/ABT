package com.example.gkaakash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gkaakash.controller.PdfGenaretor;
import com.gkaakash.controller.Report;
import com.gkaakash.controller.Startup;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import android.animation.ObjectAnimator;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.RSRuntimeException;
import android.renderscript.Sampler.Value;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    static String result;
    private Report report;
    Object[] balancesheetresult;
    String financialToDate,accountName,projectName,fromDate,toDate;
    static String balancetype, financialFromDate, balanceToDateString, getSelectedOrgType;
    TableRow tr;
    Date date;
    static ArrayList<ArrayList> BalanceSheetGrid,finalGrid_forXls;
    ArrayList<ArrayList> BalanceGrid1,BalanceGrid2,BalanceGrid;
    static ArrayList<String> balancesheetresultList;
    private TableLayout balanceSheetTable1; 
    private TableLayout balanceSheetTable2;
    private View label;
    private ArrayList<String> TotalAmountList;
    private TextView balDiff;
    String OrgName, OrgPeriod,balancePeriod,sFilename ;
    String[]pdf_params;
    private String balancefromDateString;
    Boolean updown=false;
    ScrollView sv;
    DecimalFormat formatter = new DecimalFormat("#,##,##,###.00");
    String colValue,balType;
    Boolean alertdialog = false;
    ObjectAnimator animation2;
    boolean reportmenuflag;
    static String acc_name1;
    private int group1Id = 1;
	int PDF = Menu.FIRST;
	int CSV=Menu.FIRST+1;
	module m;
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(group1Id, PDF, PDF, "PDF");
		menu.add(group1Id, CSV, CSV, "CSV");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:			
			m.generate_pdf1(balanceSheet.this, pdf_params, sFilename, BalanceGrid1,BalanceGrid2);
			return true;

		case 2:
			m.csv_writer1(BalanceGrid1,BalanceGrid2);
			m.toastValidationMessage(balanceSheet.this, "CSV exported");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
    
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
       
    	try {
    		balancetype=reportMenu.balancetype;
       
    		if(balancetype.equals("Conventional Balance Sheet"))
    		{
    			setContentView(R.layout.balance_sheet_table);
    			sv = (ScrollView)findViewById(R.id.ScrollBalanceSheet);
    			balType="Conv_bal";
    		}
    		else  
    		{
    			setContentView(R.layout.vertical_balance_sheet_table);
    			sv = (ScrollView)findViewById(R.id.ScrollVertiBalanceSheet);
    			balType="Sources_bal";
    		}
    		 if(reportmenuflag==true){ 
    	   	    	
 	    		OrgName = createOrg.organisationName;
 	    		
            }
            else {
         	    OrgName= selectOrg.selectedOrgName;
          
            }
    		//customizing title bar
    		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.bank_recon_title);
    		report = new Report();
    		client_id= Startup.getClient_id();
    		m=new module();
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
    		final Button btnPdf = (Button)findViewById(R.id.btnPdf);
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
    		
    		animated_dialog();
    		//tvfinancialFromDate.setText("Financial from : " +financialFromDate);
    		tvfinancialToDate.setText("Period : "+financialFromDate+" to "+balanceToDateString);
    		Object[] params = new Object[]{financialFromDate,financialFromDate,balanceToDateString,"balancesheet",getSelectedOrgType,balancetype};
    		balancesheetresult = (Object[]) report.getBalancesheetDisplay(params,client_id);
    		//balance sheet result is 3 dimensional list 
    		int count = 0;
    		finalGrid_forXls = new ArrayList<ArrayList>();
    		for(Object tb : balancesheetresult){
    			Object[] t = (Object[]) tb;
    			count = count + 1;
    			BalanceSheetGrid = new ArrayList<ArrayList>();
    			
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
    				//System.out.println("diff"+t[0].toString());
    				result = "Difference in Opening Balances: "+rsSymbol+" "+t[0].toString();
    				balDiff.setText(result);
    			}
    			if(count == 1){
    				addTable(balanceSheetTable1);
    				BalanceGrid1 =BalanceSheetGrid;
    			}
    			else if(count == 2){
    				addTable(balanceSheetTable2);
    				BalanceGrid2 =BalanceSheetGrid;
    			}
    		}    
    		
    		date= new Date();
			String date_format = new SimpleDateFormat("dMMMyyyy_HHmmss").format(date);
			OrgPeriod = "Financial Year:\n "+financialFromDate+" to "+financialToDate;
			balancePeriod = financialFromDate+" to "+balanceToDateString;
			sFilename = balType+"_"+date_format;
			pdf_params = new String[]{balType,sFilename,OrgName,OrgPeriod,balancetype,balancePeriod,"",result};
			btnPdf.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finalGrid_forXls.add(BalanceGrid1);
		    		finalGrid_forXls.add(BalanceGrid2);
		    		System.out.println("child:"+finalGrid_forXls.size());
		    		 
		    		
		    		
					module m=new module();
					m.generate_pdf1(balanceSheet.this, pdf_params,sFilename,BalanceGrid1,BalanceGrid2);
					m.csv_writer1(BalanceGrid1,BalanceGrid2);
				}
			});
    		drillDown();
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
    		alert.show();	}
    }
    
    
    /*
     * this method is used to add the drill down functionality on clicking the table row.
     * basically we want account name value from selected row.
     * In balancesheet we have two tables, so get the value of account name textview from selected table.
     */
    private void drillDown() {
		int count = balanceSheetTable1.getChildCount();
		for (int i = 0; i < count; i++) {
			final View row = balanceSheetTable1.getChildAt(i);
			row.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					LinearLayout l = (LinearLayout)((ViewGroup) row).getChildAt(0);
					final TextView tv = (TextView) l.getChildAt(0);
					//Toast.makeText(balanceSheet.this, tv.getText().toString(), Toast.LENGTH_SHORT).show();
					checkForAccountName(tv.getText().toString());
				}

				
			});
		}
		
		int count1 = balanceSheetTable2.getChildCount();
		for (int i = 0; i < count1; i++) {
			final View row = balanceSheetTable2.getChildAt(i);
			row.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					LinearLayout l = (LinearLayout)((ViewGroup) row).getChildAt(0);
					final TextView tv = (TextView) l.getChildAt(0);
					//Toast.makeText(balanceSheet.this, tv.getText().toString(), Toast.LENGTH_SHORT).show();
					checkForAccountName(tv.getText().toString());
				}
			});
		}
		
	}

    /*
     * below method helps to find out the selected string is account name or not.
     * initially need to check if string is in uppercase, because all the group name and non-account strings are sent 
     * in uppercase from back-end.
     * then, check for special syntax like /:'&-+^ as non-account strings can contain any of these special syntax.
     * and at last check for empty string and header column values.
     */
    private void checkForAccountName(String accname) {
    	Pattern pattern = Pattern.compile("^[A-Z]+$");//for checking whether the string is in uppercase 
    	Matcher matcher = pattern.matcher((accname.replaceAll("\\s","")).replaceAll("[/:'&-+^]*", ""));
    	boolean found = matcher.find();
    	System.out.println("value:"+found);
    	if(found == false && !(accname.equalsIgnoreCase("") || 
    							accname.equalsIgnoreCase("Total") ||
    							accname.equalsIgnoreCase("Particulars")||
    							accname.equalsIgnoreCase("Property & Assets") ||
    							accname.equalsIgnoreCase("Corpus & Liabilities"))){
    		//Toast.makeText(balanceSheet.this, "account name", Toast.LENGTH_SHORT).show();
			acc_name1 = accname;
			Intent intent = new Intent(getApplicationContext(),
					ledger.class);
			intent.putExtra("flag", "from_balanceSheet");
			startActivity(intent);
    	}
		
	}
    
    
    private void animated_dialog() {
    	try {
    		final LinearLayout Llalert = (LinearLayout)findViewById(R.id.Llalert);
            Llalert.setVisibility(LinearLayout.GONE);
            animation2 = ObjectAnimator.ofFloat(Llalert,
                    "x", 1000);
            animation2.setDuration(1000);
            animation2.start();
            
            final Button btnOrgDetailsDialog = (Button) findViewById(R.id.btnOrgDetailsDialog);
        
            btnOrgDetailsDialog.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                	btnOrgDetailsDialog.setAlpha(100);
                    if(alertdialog==false){
                        Llalert.setVisibility(LinearLayout.VISIBLE);
                        TextView tvOrgNameAlert = (TextView)findViewById(R.id.tvOrgNameAlert);
                        
                        if(reportmenuflag==true){
                            tvOrgNameAlert.setText(createOrg.organisationName);
                        }
                           else {
                               tvOrgNameAlert.setText(selectOrg.selectedOrgName);
                           }
                        
                        TextView tvOrgTypeAlert = (TextView)findViewById(R.id.tvOrgTypeAlert);
                        tvOrgTypeAlert.setText(reportMenu.orgtype);
                        
                        TextView tvFinancialYearAlert = (TextView)findViewById(R.id.tvFinancialYearAlert);
                        tvFinancialYearAlert.setText(reportMenu.financialFromDate+" to "+ reportMenu.financialToDate);
                        
                        animation2 = ObjectAnimator.ofFloat(Llalert,
                                  "x", 300);
                        alertdialog=true;
                     }else {
                        animation2 = ObjectAnimator.ofFloat(Llalert,
                                  "x", 1000);
                        alertdialog=false;
                     }
                      
                     animation2.setDuration(1000);
                     animation2.start();
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    
    private void addTable(TableLayout tableID) {
    	//System.out.println(BalanceSheetGrid);
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
    	for(int j=0;j<columnValue.size();j++)
    	{
    		System.out.println("hello :"+BalanceSheetGrid.size());
    		
    		/** Creating a TextView to add to the row **/
    		if(headerFlag == true)
    		{
    			if(j == 1 || j == 2 || j == 3||j==4){ //amount column
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
       	
           			}
               	else
               	{
               		((TextView)label).setGravity(Gravity.RIGHT);
           		
           			if(columnValue.get(j).length() > 0)
           			{
           				colValue=columnValue.get(j);
           				if(!"".equals(colValue))
           				{
	                        if(!"0.00".equals(colValue))
	                        {
	                        	Pattern pattern = Pattern.compile("\\n");
	                        	Matcher matcher = pattern.matcher(colValue);
	                        	boolean found = matcher.find();
	                        	//System.out.println("value:"+found);
	                        	if(found==false)
	                        	{
	                        		double amount1 = Double.parseDouble(colValue);	
	                        		//System.out.println("A:"+amount1);
	                        		((TextView) label).setText(formatter.format(amount1));
	                        	}else
	                        	{
	                        		((TextView) label).setText(colValue);
	                        	}
	                        }
           				}
           			}
           		}
    		}
       
    		if(j==2){//for amount coloumn

    			if(total_amount.equalsIgnoreCase("Credit")){
    				((TextView)label).setGravity(Gravity.CENTER);
    			}else{
    				((TextView)label).setGravity(Gravity.RIGHT);
    				//For adding rupee symbol
    				if(columnValue.get(j).length() > 0)
    				{	
    					colValue=columnValue.get(j);
    					if(!"".equals(colValue)){
    						//System.out.println("m in ");
    						if(!"0.00".equals(colValue)){
    							Pattern pattern = Pattern.compile("\\n");
    							Matcher matcher = pattern.matcher(colValue);
    							boolean found = matcher.find();
    							//System.out.println("value:"+found);
    							if(found==false){
    								double amount1 = Double.parseDouble(colValue);	
    								//System.out.println("A:"+amount1);
    								((TextView) label).setText(formatter.format(amount1));
    							}else {
    								((TextView) label).setText(colValue);
    							}
    						}
    					}
    				}
    			}
    		}
    		if(j==0) 
    		{
    			if(balancetype.equals("Conventional Balance Sheet"))
    			{
    				if(name.equals("CORPUS")
    						||name.equals("CAPITAL") 
    						||name.equals("RESERVES")
    						||name.equalsIgnoreCase("LOANS(Liability)")
    						||name.equals("CURRENT LIABILITIES")
    						||name.equals("FIXED ASSETS")
    						||name.equals("CURRENT ASSETS")
    						||name.equalsIgnoreCase("LOANS(Asset)")
    						||name.equals("INVESTMENTS")
    						||name.equalsIgnoreCase("MISCELLANEOUS EXPENSES(ASSET)"))
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
    						((TextView)label).setGravity(Gravity.LEFT);
    					}
	       	
    				}
    			}
    			else
    			{
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
    						||name.equalsIgnoreCase("NET CURRENT ASSETS OR WORKING CAPITAL"))
    				{
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
    						//System.out.println("m in ");
    						if(!"0.00".equals(colValue)){
    							Pattern pattern = Pattern.compile("\\n");
    							Matcher matcher = pattern.matcher(colValue);
    							boolean found = matcher.find();
    							//System.out.println("value:"+found);
    							if(found==false){
    								double amount1 = Double.parseDouble(colValue);	
    								//System.out.println("A:"+amount1);
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
    							//System.out.println("m in ");
    							if(!"0.00".equals(colValue)){
    								Pattern pattern = Pattern.compile("\\n");
    								Matcher matcher = pattern.matcher(colValue);
    								boolean found = matcher.find();
    								//System.out.println("value:"+found);
    								if(found==false){
    									double amount1 = Double.parseDouble(colValue);	
    									//System.out.println("A:"+amount1);
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
    	((TextView) label).setTextSize(18);
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
