package com.mgorshkov.hig;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.annotation.WebServlet;

import com.mgorshkov.hig.Model.Patient;
import com.mgorshkov.hig.business.SessionBean;
import com.mgorshkov.hig.entities.Alias;
import com.mgorshkov.hig.view.MainView;
import com.mgorshkov.hig.view.TableView;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
@Theme("mytheme")
@CDIUI(value = "")
@Widgetset("com.mgorshkov.hig.MyAppWidgetset")
public class MainUI extends UI {

    @Inject
    CDIViewProvider viewProvider;
    @PersistenceContext(unitName = "hig20150218")
    EntityManager entityManager;
    @EJB
    SessionBean bean;
    Navigator navigator;
    final VerticalLayout layout = new VerticalLayout();
    private Set<Patient> patientData;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        patientData = new HashSet<>();

        layout.setMargin(true);
        setContent(layout);

        setNavigation();
        navigator.navigateTo(TableView.VIEW_NAME);
    }

//    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
//    @VaadinServletConfiguration(ui = MainUI.class, productionMode = false)
//    public static class MyUIServlet extends VaadinServlet {
//    }

    private void setNavigation() {
        navigator = new Navigator(this, this);//creation du navigator en lui disant de mettre les vues dans layoutView
        navigator.addView(MainView.VIEW_NAME, MainView.class);
        navigator.addView(TableView.VIEW_NAME, TableView.class);
        navigator.addProvider(viewProvider);//provider qui va gérer les accès au vu en fonction du role de l'utilisateur
        setNavigator(navigator);
    }

    public EntityManager getEntityManager(){
        return entityManager;
    }

    public SessionBean getSessionBean(){
        return bean;
    }

    public Set<Patient> getPatientData() {
        return patientData;
    }

    public Patient isInPatientData(int setNum){
        for(Patient p : patientData){
            if(p.getPatientSerNum() == setNum){
                return p;
            }
        }
        return null;
    }

    public void setPatientData(Set<Patient> patientData) {
        this.patientData = patientData;
    }
}
