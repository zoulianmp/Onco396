package com.mgorshkov.hig.view;

import com.mgorshkov.hig.MainUI;
import com.mgorshkov.hig.model.Patient;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointClickEvent;
import com.vaadin.addon.charts.PointClickListener;
import com.vaadin.addon.charts.model.*;
import com.vaadin.cdi.CDIView;
import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Maxim Gorshkov <maxim.gorshkov<at>savoirfairelinux.com>
 */
@CDIView(value = ChartsView.VIEW_NAME)
public class ChartsView extends VerticalLayout implements View,ComboBox.ValueChangeListener {

    public final static String VIEW_NAME = "ChartsView";
    Set<Patient> workingSet = new HashSet<>();

    Label title = new Label(VIEW_NAME);

    final static String[] choices = {"Stage 1: CT Scan - Initial Contour", "Stage 2: Initial Contour - MD Contour", "Stage 3: MD Contour - CT Planning Sheet", "Stage 4: CT Planning Sheet - Dose Calculation", "Stage 5: Dose Calculation - MD Approve", "Stage 6: MD Approve - Physics QA", "Stage 7: Physics QA - Ready for Treatement"};
    ComboBox selector = new ComboBox("Stage Select: ");
    Chart chart;

    public void init(Set<Patient> workingSet){
        this.workingSet = workingSet;

        setSizeFull();
        setSpacing(true);
        setMargin(true);

        setTitle();
        setCombo();
        setCharts(0);
    }

    private void setTitle(){
        title.addStyleName(ValoTheme.LABEL_H2);
        addComponent(title);
        setExpandRatio(title, 0.1f);
    }

    private void setCombo(){
        selector.addItems(choices);
        selector.setNullSelectionAllowed(false);
        selector.setValue(choices[0]);
        selector.setWidth("100%");
        selector.addValueChangeListener(this);
        addComponent(selector);
        setExpandRatio(selector, 0.1f);
    }

    private void setCharts(int stage){
        chart = new Chart(ChartType.COLUMN);

        Configuration conf = chart.getConfiguration();
        conf.setTitle(choices[stage]);


        XAxis x = new XAxis();
        x.setCategories(patientSerNums(stage));
        x.setTitle("Patient Serial Number");
        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setTitle("Waiting Time "+(((MainUI) getUI()).getTimeUnit().toString()));
        conf.addyAxis(y);

        ArrayList<Number> values = new ArrayList<>();

        if (stage == 0) {
            for(Patient p : workingSet){
                    values.add(p.calculateFirstWait(((MainUI) getUI()).getTimeUnit(), ((MainUI) getUI()).isRemoveWeekendHolidays()));

            }
        }else if(stage == 1){
            for(Patient p : workingSet){
                    values.add(p.calculateSecondWait(((MainUI) getUI()).getTimeUnit(), ((MainUI) getUI()).isRemoveWeekendHolidays()));

            }
        }else if(stage == 2){
            for(Patient p : workingSet){
                    values.add(p.calculateThirdWait(((MainUI) getUI()).getTimeUnit(), ((MainUI) getUI()).isRemoveWeekendHolidays()));

            }
        }else if(stage == 3){
            for(Patient p : workingSet){
                    values.add(p.calculateFourthWait(((MainUI) getUI()).getTimeUnit(), ((MainUI) getUI()).isRemoveWeekendHolidays()));

            }
        }else if(stage == 4){
            for(Patient p : workingSet){
                    values.add(p.calculateFifthWait(((MainUI) getUI()).getTimeUnit(), ((MainUI) getUI()).isRemoveWeekendHolidays()));

            }
        }else if(stage == 5){
            for(Patient p : workingSet){
                    values.add(p.calculateSixthWait(((MainUI) getUI()).getTimeUnit(), ((MainUI) getUI()).isRemoveWeekendHolidays()));

            }
        }else if(stage == 6){
            for(Patient p : workingSet){
                    values.add(p.calculateSeventhWait(((MainUI) getUI()).getTimeUnit(), ((MainUI) getUI()).isRemoveWeekendHolidays()));

            }
        }

        Number[] valuesToAdd = new Number[values.size()];
        for(int i = 0; i<valuesToAdd.length; i++){
            valuesToAdd[i] = values.get(i);
        }

        conf.setSeries(new ListSeries("", valuesToAdd));
        chart.drawChart(conf);

        chart.setSizeFull();

        chart.addPointClickListener(new PointClickListener() {
            @Override
            public void onClick(PointClickEvent pointClickEvent) {
                ((MainUI) getUI()).setCrtUser(pointClickEvent.getCategory());
                getUI().getNavigator().navigateTo(PatientView.VIEW_NAME);
            }
        });

        addComponent(chart);

        setExpandRatio(chart, 0.8f);

    }

    private String[] patientSerNums(int stage){
        ArrayList<String> sers = new ArrayList<>();

        switch(stage){
            case 0 : for(Patient p : workingSet){
                         sers.add("" + p.getPatientSerNum());
                     }
                     break;
            case 1 : for(Patient p : workingSet){
                    sers.add("" + p.getPatientSerNum());
            }
                break;
            case 2 : for(Patient p : workingSet){
                    sers.add("" + p.getPatientSerNum());
            }
                break;
            case 3 : for(Patient p : workingSet){
                    sers.add("" + p.getPatientSerNum());
            }
                break;
            case 4 : for(Patient p : workingSet){
                    sers.add("" + p.getPatientSerNum());
            }
                break;
            case 5 : for(Patient p : workingSet){
                    sers.add("" + p.getPatientSerNum());
            }
                break;
            case 6 : for(Patient p : workingSet){
                    sers.add("" + p.getPatientSerNum());
            }
                break;
        }


        String[] serialToReturn = new String[sers.size()];

        for(int i = 0; i<sers.size(); i++){
            serialToReturn[i] = sers.get(i);
        }

        return serialToReturn;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        removeAllComponents();
        if(workingSet == null){
            getUI().getNavigator().navigateTo(MainView.VIEW_NAME);
        }
        init(((MainUI) getUI()).getPatientData());
    }

    @Override
    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
        removeComponent(chart);
        if(selector.getValue().toString().equals(choices[0])){
            setCharts(0);
        }else if(selector.getValue().toString().equals(choices[1])){
            setCharts(1);
        }else if(selector.getValue().toString().equals(choices[2])){
            setCharts(2);
        }else if(selector.getValue().toString().equals(choices[3])){
            setCharts(3);
        }else if(selector.getValue().toString().equals(choices[4])){
            setCharts(4);
        }else if(selector.getValue().toString().equals(choices[5])){
            setCharts(5);
        }else if(selector.getValue().toString().equals(choices[6])){
            setCharts(6);
        }
    }
}

