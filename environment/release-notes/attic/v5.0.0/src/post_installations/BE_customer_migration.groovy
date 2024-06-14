import java.util.*;
import org.apache.commons.lang.StringUtils;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


flexibleSearchService = ctx.getBean("flexibleSearchService");
modelService = ctx.getBean("modelService");


// erpCustomerIDs = "('1261194', '1261687', '1262097', '1262118', '1262842', '1262862', '1263238', '1263934', '1263942', '1264278', '1266930', '1267176', '1267275', '1267426', '1267431', '1268419', '1268421', '1268424', '1271220', '1272378', '1274056', '1274778', '1274780', '1277773', '1277943', '1278235', '1278759', '1278935', '1279200', '1279201', '1279242', '1279430', '1279695', '1280249', '1281050', '1281755', '1281823', '1282138', '1282374', '1285629', '1285696', '1285812', '1285913', '1286038', '1286897', '1287060', '1287422', '1287479', '1287611', '1287768', '1290926', '1291395', '1291396', '1291592', '1291756', '1292529', '1292624', '1292625', '1293231', '1293704', '1296566', '1297604', '1299047', '1299049', '1300869', '1301019', '1302281', '1302555', '1303477', '1303618', '1304298', '1305025', '1305639', '1305990', '2094440', '2094441', '2097310', '2113986', '2118787', '2123251', '2124148', '2125416', '2179063', '2185819', '2199756', '2203811', '2206337', '2207420', '2207584', '2213159', '2214592', '2223258', '2246113', '2259552', '2259556', '2260619', '2261462', '2263205', '2264086', '2266580', '2270268', '2270603', '2272944', '2276383', '2278918', '2284738', '2285762', '2290347', '2291756', '2294826', '2296052', '2297724', '2301263', '2301269', '2306418', '2311393', '2311939', '2313802', '2314441', '2318325', '2319517', '2319641', '2321093', '2321204', '2322490', '2322905', '2325009', '2325713', '2325851', '2326782', '2328876', '2328967', '2328969', '2329147', '2329279', '2329637', '2329645', '2329648', '2330739', '2331735', '2332202', '2332341', '2332603', '2332699', '2332702', '2333122', '2333159', '2333874', '2334349', '2334355', '2334404', '2334416', '2334421', '2334536', '2334746', '2334973', '2335188', '2335831', '2336461', '2337002', '2337158', '2337188', '2337765', '2337786', '2337987', '2338821', '2339024', '2339341', '2339459', '2339703', '2340152', '2340315', '2340456', '2341070', '2341366', '2341570', '2341656', '2341756', '2341800', '2342044', '2342818', '2343201', '2343599', '2344069', '2344398', '2345126', '2345503', '2345530', '2345541', '2345623', '2345814', '2346172', '2346785', '2347254', '2347622', '2347633', '2347968', '2348399', '2348518', '2348804', '2348854', '2349097', '2349343', '2349543', '2349603', '2349640', '2349842', '2350350', '2350400', '2350873', '2350952', '2350988', '2351639', '2352348', '2352531', '2353449', '2353584', '2353611', '2354518', '2354560', '2355442', '2356476', '2357305', '2357538', '2360722', '2361733', '2361735', '2361738', '2361853', '2361933', '2362155', '2363782', '2364056', '2364904', '2365117', '2366022', '2367137', '2368010', '2368900', '2369149', '2369769', '2372386', '2373484', '2373559', '2373698', '2374224', '2374729', '2375493', '2376787', '2381505', '2382170', '2386092', '2386814', '2388343', '2388629', '2390126', '2394882', '2394996', '2395232', '0001261194', '0001261687', '0001262097', '0001262118', '0001262842', '0001262862', '0001263238', '0001263934', '0001263942', '0001264278', '0001266930', '0001267176', '0001267275', '0001267426', '0001267431', '0001268419', '0001268421', '0001268424', '0001271220', '0001272378', '0001274056', '0001274778', '0001274780', '0001277773', '0001277943', '0001278235', '0001278759', '0001278935', '0001279200', '0001279201', '0001279242', '0001279430', '0001279695', '0001280249', '0001281050', '0001281755', '0001281823', '0001282138', '0001282374', '0001285629', '0001285696', '0001285812', '0001285913', '0001286038', '0001286897', '0001287060', '0001287422', '0001287479', '0001287611', '0001287768', '0001290926', '0001291395', '0001291396', '0001291592', '0001291756', '0001292529', '0001292624', '0001292625', '0001293231', '0001293704', '0001296566', '0001297604', '0001299047', '0001299049', '0001300869', '0001301019', '0001302281', '0001302555', '0001303477', '0001303618', '0001304298', '0001305025', '0001305639', '0001305990', '0002094440', '0002094441', '0002097310', '0002113986', '0002118787', '0002123251', '0002124148', '0002125416', '0002179063', '0002185819', '0002199756', '0002203811', '0002206337', '0002207420', '0002207584', '0002213159', '0002214592', '0002223258', '0002246113', '0002259552', '0002259556', '0002260619', '0002261462', '0002263205', '0002264086', '0002266580', '0002270268', '0002270603', '0002272944', '0002276383', '0002278918', '0002284738', '0002285762', '0002290347', '0002291756', '0002294826', '0002296052', '0002297724', '0002301263', '0002301269', '0002306418', '0002311393', '0002311939', '0002313802', '0002314441', '0002318325', '0002319517', '0002319641', '0002321093', '0002321204', '0002322490', '0002322905', '0002325009', '0002325713', '0002325851', '0002326782', '0002328876', '0002328967', '0002328969', '0002329147', '0002329279', '0002329637', '0002329645', '0002329648', '0002330739', '0002331735', '0002332202', '0002332341', '0002332603', '0002332699', '0002332702', '0002333122', '0002333159', '0002333874', '0002334349', '0002334355', '0002334404', '0002334416', '0002334421', '0002334536', '0002334746', '0002334973', '0002335188', '0002335831', '0002336461', '0002337002', '0002337158', '0002337188', '0002337765', '0002337786', '0002337987', '0002338821', '0002339024', '0002339341', '0002339459', '0002339703', '0002340152', '0002340315', '0002340456', '0002341070', '0002341366', '0002341570', '0002341656', '0002341756', '0002341800', '0002342044', '0002342818', '0002343201', '0002343599', '0002344069', '0002344398', '0002345126', '0002345503', '0002345530', '0002345541', '0002345623', '0002345814', '0002346172', '0002346785', '0002347254', '0002347622', '0002347633', '0002347968', '0002348399', '0002348518', '0002348804', '0002348854', '0002349097', '0002349343', '0002349543', '0002349603', '0002349640', '0002349842', '0002350350', '0002350400', '0002350873', '0002350952', '0002350988', '0002351639', '0002352348', '0002352531', '0002353449', '0002353584', '0002353611', '0002354518', '0002354560', '0002355442', '0002356476', '0002357305', '0002357538', '0002360722', '0002361733', '0002361735', '0002361738', '0002361853', '0002361933', '0002362155', '0002363782', '0002364056', '0002364904', '0002365117', '0002366022', '0002367137', '0002368010', '0002368900', '0002369149', '0002369769', '0002372386', '0002373484', '0002373559', '0002373698', '0002374224', '0002374729', '0002375493', '0002376787', '0002381505', '0002382170', '0002386092', '0002386814', '0002388343', '0002388629', '0002390126', '0002394882', '0002394996', '0002395232')";

// u_query = "SELECT {pk} FROM {B2BUnit AS un JOIN DistSalesOrg AS dso ON {un.salesOrg}={dso.pk}} WHERE {dso.code}='7801' AND {erpCustomerID} IN " + erpCustomerIDs

u_query = "SELECT {pk} FROM {B2BUnit AS un JOIN DistSalesOrg AS dso ON {un.salesOrg}={dso.pk} JOIN Country AS c ON {c.pk}={un.country}} WHERE {dso.code}='7801' AND {c.isocode}='BE'"

so_query = "SELECT {pk} FROM {DistSalesOrg} WHERE {code}='7800'";

success = 0;
total = 0;

try {
	companies = flexibleSearchService.<B2BUnitModel>search(u_query).getResult();
	if(companies != null && companies.size() > 0) {
		total = companies.size();
		// Search for the NL Sales Org.
		nl_sorg = flexibleSearchService.<DistSalesOrgModel>searchUnique(new FlexibleSearchQuery(so_query));
		companies.each {
			it.setSalesOrg(nl_sorg);
			modelService.save(it);
			success++;
		}
	}
} catch(Exception ex) {
  	println "ERROR --> " + ex.getMessage();
}

println "";
println "End of program with following statistics:";
println " * Success: " + success;
println " * total: " + total;