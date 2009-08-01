/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.providers;


import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.EntityView;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityHttpParam;
import org.sakaiproject.entitybus.entityprovider.capabilities.ActionsExecutable;
import org.sakaiproject.entitybus.entityprovider.capabilities.Describeable;
import org.sakaiproject.entitybus.entityprovider.capabilities.ActionsExecutionControllable;
import java.io.OutputStream;
import java.util.Map;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybus.entityprovider.extension.CustomAction;

/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class TestEntityProvider extends AbstractRESTProvider implements CoreEntityProvider, ActionsExecutable, Describeable, ActionsExecutionControllable {
 public TestEntityProvider(EntityProviderManager entityProviderManager) {
        super(entityProviderManager);
    }

    @EntityCustomAction(action="postoji", viewKey=EntityView.VIEW_SHOW)
    public String exists(EntityReference ref, EntityView view, Map<String, Object> params) {
        System.out.println("Rezultat je: ");
        String id = ref.getId();
        return "Zdravo: " + id + ", moje ime je :" + view.toString() + "|" + params.toString();
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entitybus.entityprovider.CoreEntityProvider#entityExists(java.lang.String)
     */
    public boolean entityExists(String id) {
        boolean exists = false;
        if (id.equals("AZ")) {
            exists = true;
        }
        return exists;
    }

    public Object executeActions(EntityView entityView, String action, Map<String, Object> actionParams, OutputStream outputStream) {
    System.out.println("akcije");
    System.out.println("action:" + action + " length map: " + actionParams.toString());
    System.out.println(entityView.toString());
    
        return null;
    }

    public CustomAction[] defineActions() {
        return new CustomAction[] {
            new CustomAction("tema",EntityView.VIEW_SHOW,"tema"),
            new CustomAction("/",EntityView.VIEW_SHOW,"a")
        };
    }

//    public Object tema(EntityView view) {
//        System.out.println("tema startovana");
//        return null;
//    }

    public Object getEntity(EntityReference reference) {
        System.out.println("getting entity");
        return new StandardEntity();
    }
    
    /* (non-Javadoc)
     * @see org.sakaiproject.entitybus.entityprovider.EntityProvider#getEntityPrefix()
     */
    public String getEntityPrefix() {
        return "prvitest";
    }

}
