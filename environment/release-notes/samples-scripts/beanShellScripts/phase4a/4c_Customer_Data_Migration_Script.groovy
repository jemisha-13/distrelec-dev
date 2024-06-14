import java.util.*
import de.hybris.platform.servicelayer.search.*
import de.hybris.platform.servicelayer.search.impl.*
import de.hybris.platform.b2b.model.*
import de.hybris.platform.core.*
import de.hybris.platform.jalo.*
import de.hybris.platform.jalo.user.*
import de.hybris.platform.jalo.flexiblesearch.*
import com.namics.hybris.toolbox.spring.SpringUtil
import de.hybris.platform.servicelayer.model.ModelService
import org.apache.commons.lang.StringUtils
import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import de.hybris.platform.b2b.model.B2BUnitModel
import de.hybris.platform.core.model.user.AddressModel
import de.hybris.platform.b2b.model.B2BCustomerModel
import java.util.Date
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar


  
if (!Registry.hasCurrentTenant()) {
    Registry.activateMasterTenant()
}

JaloSession.getCurrentSession().setUser(UserManager.getInstance().getUserByLogin("admin"))
modelService = (ModelService) SpringUtil.getBean("modelService")
flexibleSearchService = (FlexibleSearchService) SpringUtil.getBean("flexibleSearchService")

salesOrgList = ['7310']
  
semicolon = ';'
comma = ','  
replacechar = '#'

qexportoath = '/data_nfs/migration_data_export/q_export'
pexportoath = '/data_nfs/migration_data_export/p_export'
localexportoath = System.getProperty('java.io.tmpdir')

exportpath = pexportoath

dummydate = Date.parse('yyyy-MM-dd','2000-01-01')

def getFormatedDate(){
    return new SimpleDateFormat('yyyyMMdd').format(new Date())
}


synchronized  writetofile(str, filename) {
    try {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename), true), 'UTF8'))
        out.append(str)
        out.flush()
        out.close()

    } catch (Exception e) {
        println(e.getMessage())
    }
}
def cleanup(filename)
{
 try{
		file = new File(filename)
		if(file.delete()){
			System.out.println("${file.getName()} is deleted!")
		}
	}catch(Exception e){
		e.printStackTrace()
	}
}

synchronized removeExtraComma(data)
{
   if((data?.length() > 0) && (data?.length() == (data?.lastIndexOf(comma)+1)))
   {
       data.deleteCharAt(data.lastIndexOf(comma))
   }
}

synchronized appendData(sb, data, sep)
{
  if(data?.equals(''))
  {
  	sb?.append(sep)
  }	
  else
  {
  	sb?.append(data?.replaceAll(semicolon, replacechar)).append(sep)
  }
}


synchronized appendAddressData(address_data, address, unit)
{
  address_data << "${address?.getPk()?.toString()}$semicolon" 
  address_data << "${unit?.getPk()?.toString()}$semicolon"
  address_data << "${address?.getCompany()}$semicolon"
  address_data << "${address?.getCompanyName2()}$semicolon"
  address_data << "${address?.getCompanyName3()}$semicolon"
  address_data << "${address?.getStreetname()}$semicolon"
  address_data << "${address?.getStreetnumber()}$semicolon"
  address_data << "${address?.getPobox()}$semicolon"
  address_data << "${address?.getPostalcode()}$semicolon"
  address_data << "${address?.getTown()}$semicolon"
  address_data << "${address?.getRegion()?.getIsocode()}$semicolon"
  address_data << "${address?.getCountry()?.getIsocode()}$semicolon"
  address_data << "${address?.getBillingAddress()?.toString()}$semicolon"
  address_data << "${address?.getShippingAddress()?.toString()}$semicolon"
  address_data << "${address?.getContactAddress()?.toString()}$semicolon"
  address_data << "\r"
}

synchronized appendContactData(contact_data, contact, unit)
{
    contact_data << "${contact?.getPk()?.toString()}$semicolon"
    contact_data << "${unit?.getPk()?.toString()}$semicolon"
    contact_data << "${contact?.getTitle()?.getCode()}$semicolon"

	contact_data << "${contact?.getContactAddress()?.getFirstname()}$semicolon"
	contact_data << "${contact?.getContactAddress()?.getLastname()}$semicolon"
	contact_data << "${contact?.getContactAddress()?.getAdditionalAddressCompany()}$semicolon"

	contact_data << "${contact?.getContactAddress()?.getDistDepartment()?.getCode()}$semicolon"
	contact_data << "${contact?.getContactAddress()?.getPhone1()}$semicolon"
	contact_data << "${contact?.getContactAddress()?.getCellphone()}$semicolon"
	contact_data << "${contact?.getContactAddress()?.getFax()}$semicolon"

	contact_data << "${contact?.getEmail()}$semicolon"
	contact_data << "${contact?.getDistFunction()?.getCode()}$semicolon"
    contact_data << "${unit?.getErpCustomerID()}$semicolon"
    contact_data << "${contact?.getLastLogin()?.toLocaleString()}$semicolon"
	contact_data << "\r"	
}

try
{
    salesOrgList.each{
        salesorg -> salesOrgList
        println("running for salesorg $salesorg")
    
    	cust_filename = "$exportpath${File.separator}${salesorg}_Customer_export_${getFormatedDate()}.csv"
    	cont_filename = "$exportpath${File.separator}${salesorg}_Contact_export_${getFormatedDate()}.csv"
    	address_filename ="$exportpath${File.separator}${salesorg}_Address_export_${getFormatedDate()}.csv"
    	
    	cleanup(cust_filename)
    	cleanup(cont_filename)
    	cleanup(address_filename)
    	
    	writetofile("Customer_Id;ErpCustomer_Id;SalesOrganization;CustomerType;VatId;Name;OrganizationNumber;ContactIds;AddressIds\r",cust_filename)
    	writetofile("Contact_Id;Customer_Id;Title;FirstName;LastName;AdditionalName;Department;PhoneNumbers;Mobilenumber;Faxnumber;Email;Function;ErpCustomerID;LastLoginDate\r",cont_filename)
    	//writetofile("Address_Id;Customer_Id;CompanyName1;CompanyName2;CompanyName3/AdditionalName;StreetName;StreetNumber;Pobox;PostalCode;Town;Region;Country;isBilling;isShipping;isContact\r",address_filename)
    	
    
    	query = """SELECT distinct innerSelect.pk FROM 
                    ({{
                        SELECT {bu.pk} FROM { 
                                    B2BUnit AS bu JOIN  DistSalesOrg AS sorg ON {bu.salesOrg} = {sorg.pk}
                                    JOIN MovexOrder AS mo ON {mo.distCustomerNr} = {bu.erpcustomerid}
                              }
                              WHERE  {sorg.code} = '${salesorg}'  AND 
                                    {mo.orderPlacedDate} >= ADD_MONTHS(TRUNC(sysdate),-24)
                                     AND {customerType} not IN ({{SELECT {pk} FROM {customerType} WHERE {code} =  'GUEST'}})
                                     AND {bu.erpCustomerID} IS NOT NULL
                    }}
                        UNION ALL {{
                              SELECT {bu.pk} FROM {B2BUnit as bu JOIN  DistSalesOrg AS sorg on {bu.salesOrg} = {sorg.pk}}
                              WHERE {sorg.code} = '${salesorg}'
                              AND {bu.customerType} IN ({{SELECT {pk} FROM {customerType} WHERE {code} IN ('OCI', 'ARIBA', 'CXML')}})
                              AND {bu.erpCustomerID} IS NOT NULL
                        }} 
                    ) innerSelect"""   
                   
    	  
    	println(query)
    
    	b2bUnits = flexibleSearchService.search(query).getResult()
    
    	b2bUnits.each{
    		 unit -> b2bUnits
    	     customer_data = new StringBuffer()
    	     address_data = new StringBuffer()
    	     contact_data = new StringBuffer()
           
    	
    		 customer_data << "${unit?.getPk()?.toString()}$semicolon"
    		 customer_data << "${unit?.getErpCustomerID()}$semicolon"
    		 customer_data << "${unit?.getSalesOrg()?.getCode()}$semicolon"	    
    		 customer_data << "${unit?.getCustomerType()?.getCode()}$semicolon"
    		 customer_data << "${unit?.getVatID()}$semicolon"
    		 customer_data << "${unit?.getName()}$semicolon"
    		 customer_data << "${unit?.getOrganizationalNumber()}$semicolon"
    		
             def members= []
               members.addAll(unit?.getMembers())
               
            if(members?.size()>1){
                members?.sort{m1,m2->
                    if(m1?.getLastLogin() != null && m2.getLastLogin() != null){
                      m1?.getLastLogin()?.compareTo(m2?.getLastLogin())
                    }else if(m1?.getLastLogin() == null || m2?.getLastLogin() == null){
                        (m1.getLastLogin() == null ? dummydate : m1.getLastLogin()).compareTo(m2.getLastLogin() == null ? dummydate : m2.getLastLogin())
                    }
                  }
            }
            
            members?.each{
    	        if (it instanceof B2BCustomerModel) {
    	            contact = (B2BCustomerModel) it
                    if(!contact.getUid().contains('|')){ // bug exclude guest accounts
                	    appendData(customer_data,contact.getPk().toString(),comma)
                        appendContactData(contact_data,contact,unit)
                    }
    	        }
    	    }
    	    
    	    removeExtraComma(customer_data)
    	    customer_data << "$semicolon"
    	    
    	    unit?.getAddresses()?.each{
    	          appendData(customer_data,it.getPk().toString(),comma)
    	          appendAddressData(address_data,it,unit)
    	       }      
    	      
    	    removeExtraComma(customer_data)      
    	    customer_data << "\r"
    	       
    	    writetofile(customer_data.toString(),cust_filename)
    	   // writetofile(address_data.toString(),address_filename)
    	    writetofile(contact_data.toString(),cont_filename)
       } // end of b2bUnits loop
    
       println("Done, check file --> $cust_filename")
       println("Done, check file --> $address_filename")
       println("Done, check file --> $cont_filename")
    }// end of main loop
}
catch(Exception ex)
{
    ex.printStackTrace();
}

