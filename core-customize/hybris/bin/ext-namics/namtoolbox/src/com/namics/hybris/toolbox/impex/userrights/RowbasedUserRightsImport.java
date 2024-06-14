package com.namics.hybris.toolbox.impex.userrights;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.namics.hybris.toolbox.FileUtils;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.security.AccessManager;
import de.hybris.platform.jalo.security.ImportExportUserRightsHelper;
import de.hybris.platform.jalo.security.Principal;
import de.hybris.platform.jalo.security.UserRight;
import de.hybris.platform.jalo.type.AttributeDescriptor;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.util.CSVReader;
import de.hybris.platform.util.localization.Localization;

/**
 * <p>
 * Executes an import of access rights for hybris types and attributes per principal. Instead of hybris's default two-line format, a
 * one-line format is used.
 * </p>
 * 
 * <p>
 * Hybris provides a standard impex functionality to import item and attribute rights. The drawback is, that this import format works with
 * two lines per permission. <br>
 * There is always a group specific line (like '<code>UserGroup;administratorgroup;;;;;;;;</code>') and some permission specific lines (like
 * '<code>;;;;UserGroup;+;+;+;+</code>').
 * 
 * Here an example:
 * 
 * <pre>
 * # this user rights definition relates to CORE-4072;;;;;;;;;
 * $START_USERRIGHTS;;;;;;;;;
 * Type;UID;MemberOfGroups;Password;Target;read;change;create;remove;change_perm
 * UserGroup;employeegroup;;;;;;;;
 * ;;;;Item;+;-;-;-;-
 * 
 * UserGroup;administratorgroup;;;;;;;;
 * ;;;;UserGroup;+;+;+;+
 * ;;;;Employee;+;+;+;+
 * ;;;;Customer;+;+;-;-
 * 
 * $END_USERRIGHTS;;;;;
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * This imported provides the support for a one-line format. In one line is the principal definition and the permissions for a target type
 * or type attribute. <br>
 * Here the example from above in the one-line format:
 * 
 * <pre>
 * # this user rights definition relates to CORE-4072;;;;;;;;;
 * $START_USERRIGHTS;;;;;;;;;
 * Type;UID;MemberOfGroups;Password;Target;read;change;create;remove;change_perm
 * UserGroup;employeegroup;;;Item;+;-;-;-;-
 * 
 * UserGroup;administratorgroup;;;UserGroup;+;+;+;+
 * UserGroup;administratorgroup;;;Employee;+;+;+;+
 * UserGroup;administratorgroup;;;Customer;+;+;-;-
 * 
 * $END_USERRIGHTS;;;;;
 * </pre>
 * 
 * </p>
 * <p>
 * The one-line format is necessary if you receive a excel file with all permissions from the customer and have to build an impex out of the
 * excel spreadsheet. It is difficult to expand the one-line format of excel into the hybris standard accessrights impex format.
 * </p>
 * <p>
 * This importer doesn't support the hybris format and doesn't support the hybris impex features like beanshell, macros etc. Lines starting
 * with '#' and '$' are ignored. The first line must be the header line
 * <code>Type;UID;MemberOfGroups;Password;Target;read;change;create;remove;change_perm</code>. All following lines must be in CSV format. <br>
 * Meaning of the permission symbols:
 * <ul>
 * <li>'+': The permission is explicitly set as allowed.</li>
 * <li>'-': The permission is explicitly set as disallowed.</li>
 * <li>'': The permission is cleared from the attribute or type.</li>
 * </p>
 * 
 * @see ImportExportUserRightsHelper
 * @author jonathan.weiss, namics ag
 * @since MGB PIM 1.0
 * 
 */
public class RowbasedUserRightsImport {

    /** Used logger instance. */
    private static final Logger LOG = Logger.getLogger(RowbasedUserRightsImport.class.getName());

    /**
     * The list of files with user rights to import.
     */
    protected List<String> filelist = new ArrayList<String>();

    /**
     * Is the import aborted by an exception if a user or a type couldn't be found.
     */
    protected boolean abortOnError;

    private UserRight read;
    private UserRight change;
    private UserRight create;
    private UserRight remove;
    private List<UserRight> listOfRights;

    /**
     * Initialize the hybris user rights (read,change,create,remove).
     */
    private void initialize() {
        read = AccessManager.getInstance().getOrCreateUserRightByCode("read");
        change = AccessManager.getInstance().getOrCreateUserRightByCode("change");
        create = AccessManager.getInstance().getOrCreateUserRightByCode("create");
        remove = AccessManager.getInstance().getOrCreateUserRightByCode("remove");
        // UserRight changeperm = AccessManager.getInstance().getOrCreateUserRightByCode("changerights");

        listOfRights = new ArrayList<UserRight>();
        listOfRights.add(read);
        listOfRights.add(change);
        listOfRights.add(create);
        listOfRights.add(remove);

    }

    /**
     * Executes the hmc xml import.
     * 
     * @throws Exception
     *             exception like <code>FileNotFoundException</code>.
     */
    public void performTask() throws Exception {
        LOG.info("Start importing " + filelist.size() + " userrights files ...");
        for (final String filepath : filelist) {
            LOG.info("Import " + filepath);
            final Resource resource = FileUtils.createResourceFromFilepath(filepath, true);
            final Reader fileReader = new InputStreamReader(resource.getInputStream());
            readFile(fileReader);
            fileReader.close();
        }
        LOG.info("Import of userrights file finished.");

    }

    /**
     * Reads the file and writes access rights to the data base.
     * 
     * @param fileReader
     *            A java.io.Reader to read the permissions.
     */
    protected void readFile(final Reader fileReader) {
        final CSVReader csvreader = new CSVReader(fileReader);
        initialize();
        try {

            Map<Integer, String> csvlinemap = null;

            // Reading the header part of the file
            while (csvreader.readNextLine()) {
                csvlinemap = csvreader.getLine();

                if (csvlinemap == null) {
                    throw new JaloInvalidParameterException("could not parse file", 5557);
                }

                // Validate first line
                if (!csvlinemap.get(Integer.valueOf(0)).startsWith("#") && !csvlinemap.get(Integer.valueOf(0)).startsWith("$")) {
                    if (!csvlinemap.containsValue("Type") || !csvlinemap.containsValue("UID") || !csvlinemap.containsValue("MemberOfGroups")
                            || !csvlinemap.containsValue("Password") || !csvlinemap.containsValue("Target")) {
                        throw new JaloInvalidParameterException(Localization.getLocalizedString("importexportuserrightshelper.import.exception.description"),
                                5556);
                    } else {
                        // we have a valid header found, go to the data.
                        break;
                    }
                }
            }

            // Reading the data part of the file
            while (csvreader.readNextLine()) {
                csvlinemap = csvreader.getLine();
                if (csvlinemap != null && !csvlinemap.get(Integer.valueOf(0)).startsWith("#") && !csvlinemap.get(Integer.valueOf(0)).startsWith("$")) {
                    readLine(csvlinemap);
                }
            }
        } finally {
            try {
                csvreader.close();
            } catch (final IOException e) {
                // ignore, we were closing the reader
            }
        }

    }

    /**
     * Read one CSV line from the file into the database.
     * 
     * @param csvData
     */
    protected void readLine(final Map<Integer, String> csvData) {

        LOG.debug(csvData);
        final String principalUID = csvData.get(Integer.valueOf(1));
        final String targetString = csvData.get(Integer.valueOf(4));
        final String readString = csvData.get(Integer.valueOf(5));
        final String changeString = csvData.get(Integer.valueOf(6));
        final String createString = csvData.get(Integer.valueOf(7));
        final String removeString = csvData.get(Integer.valueOf(8));

        try {
            final Principal principal = getPrincipal(principalUID);
            final Item target = getTargetObject(targetString);

            // First remove all rights
            removeRight(principal, target, listOfRights);

            // Then assign rights
            assignRight(principal, target, read, readCSVPermissionValue(readString));
            assignRight(principal, target, change, readCSVPermissionValue(changeString));
            assignRight(principal, target, create, readCSVPermissionValue(createString));
            assignRight(principal, target, remove, readCSVPermissionValue(removeString));
        } catch (final JaloItemNotFoundException e) {
            if (isAbortOnError()) {
                throw e;
            } else {
                LOG.error("A jalo item couldn't be found (for call " + principalUID + "," + targetString + "): '" + e.getMessage() + "'");
                // continue
            }
        }

    }

    /**
     * Removes rights from an item for a principal.
     * 
     * @param principal
     *            The principal to assign the rights
     * @param target
     *            The target item or attribute of an item to assign the rights, for example <code>Product</code> or
     *            <code>Product.code</code>
     * @param rights
     *            A list of rights to remove from <code>target</code> for <code>principal</code>.
     */
    protected void removeRight(final Principal principal, final Item target, final List<UserRight> rights) {
        // not implemented
    }

    /**
     * Assign a single right to an item for a principal.
     * 
     * @param principal
     *            The principal to assign the rights
     * @param target
     *            The target item or attribute of an item to assign the rights, for example <code>Product</code> or
     *            <code>Product.code</code>
     * @param right
     *            The right to assign.
     * @param negative
     *            <code>true</code> (=permit the right), <code>false</code> (=disallow/restrict the right) or <code>null</code> (do not
     *            change)
     */
    protected void assignRight(final Principal principal, final Item target, final UserRight right, final Boolean negative) {
        LOG.debug("Set on {" + principal + "} for {" + target + "} the right {" + right + "} to {" + negative + " (negative)}.");

        if (negative == null) {
            // Negative can be null, but it is a permitted value.
            target.clearPermission(principal, right);
        } else {
            // System.out.println("Before: " + target.checkPermission(principal, right));
            target.addPermission(principal, right, negative.booleanValue());
            // System.out.println("After: " + target.checkPermission(principal, right));
        }
    }

    /**
     * @see ImportExportUserRightsHelper#readCSVPermissionValue.
     */
    private Boolean readCSVPermissionValue(final String o) {
        if ("+".equals(o)) {
            return Boolean.FALSE;
        }
        if ("-".equals(o)) {
            return Boolean.TRUE;
        } else {
            return null;
        }
    }

    /**
     * The string in <code>typename</code> is a Item (e.g. 'Product') or a Item-Attribute (e.g. 'Product.code'). Returns the descriptor of
     * this item or attribute.
     * 
     * @see ImportExportUserRightsHelper#setTypeRights
     */
    protected Item getTargetObject(final String typename) {
        Item composedType = null;
        if (typename.indexOf('.') == -1) {
            composedType = JaloSession.getCurrentSession().getTypeManager().getComposedType(typename);
        } else {
            final String type = typename.substring(0, typename.indexOf('.'));
            final String attr = typename.substring(typename.indexOf('.') + 1, typename.length());
            final ComposedType x = JaloSession.getCurrentSession().getTypeManager().getComposedType(type);
            final AttributeDescriptor attrDesc = x.getAttributeDescriptor(attr);
            composedType = attrDesc;
        }
        return composedType;

    }

    /**
     * The string in <code>uid</code> is a principal UID. Returns a user or a user group.
     */
    protected Principal getPrincipal(final String uid) {
        try {
            return UserManager.getInstance().getUserGroupByGroupID(uid);
        } catch (final JaloItemNotFoundException e) {
            return UserManager.getInstance().getUserByLogin(uid);
        }
    }

    public void setFilelist(final List<String> filelist) {
        this.filelist = filelist;
    }

    public boolean isAbortOnError() {
        return abortOnError;
    }

    public void setAbortOnError(final boolean abortOnError) {
        this.abortOnError = abortOnError;
    }

}
