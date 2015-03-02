package com.mgorshkov.hig.filters;

import com.mgorshkov.hig.MainUI;
import com.mgorshkov.hig.entities.Appointment;
import com.mgorshkov.hig.entities.Document;
import com.mgorshkov.hig.entities.Task;
import com.mgorshkov.hig.model.DataPoint;
import com.mgorshkov.hig.model.DataPointType;
import com.mgorshkov.hig.model.Patient;
import com.vaadin.ui.UI;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Maxim Gorshkov <maxim.gorshkov<at>savoirfairelinux.com>
 */
public class AddAllData {

    @PersistenceContext(unitName = "hig20150218")
    private EntityManager entityManager;
    private Set<Patient> workingSet = new HashSet<>();

    boolean setDebug = true;

    public AddAllData(){

        setEntityManager();

        if(setDebug){
            addAllDebug();
        }else{
            //addAll();
        }

    }

    private void addAllDebug(){
        long start = System.currentTimeMillis();
        System.out.println("Start: "+workingSet.size());

        addAppointment();

        System.out.println("End: "+workingSet.size());
        System.out.println("Elapsed: "+(System.currentTimeMillis() - start)+ " ms");

        long start2 = System.currentTimeMillis();

        addTasks();

        System.out.println("Elapsed: "+(System.currentTimeMillis() - start2)+ " ms");

        long start3 = System.currentTimeMillis();

        addDocuments();

        System.out.println("Elapsed: "+(System.currentTimeMillis() - start3)+ " ms");

    }

    private void addAppointment(){

        TypedQuery<Appointment> directQuery = entityManager.createNamedQuery("Appointment.findByAliasAndStatus", Appointment.class);
        directQuery.setParameter("aSerNum", new BigInteger("3"));
        directQuery.setParameter("aStatus", "Cancelled");
        directQuery.setParameter("aStatus2", "Open");

        List<Appointment> directList = directQuery.getResultList();

        for(Appointment a : directList){
            Patient ifExists = isInPatientData(a.getPatientSerNum());

            if(ifExists == null){
                Patient newPatient = new Patient(a.getPatientSerNum());
                newPatient.addDataPoint(new DataPoint(a.getScheduledStartTime(), DataPointType.APPOINTMENT));
                workingSet.add(newPatient);
            }else{
                ifExists.addDataPoint(new DataPoint(a.getScheduledStartTime(), DataPointType.APPOINTMENT));
            }
        }

    }

    private void addTasks(){

        TypedQuery<Task> directQuery = entityManager.createNamedQuery("Task.findByAliasAndStatus", Task.class);
        directQuery.setParameter("aSerNum", new BigInteger("17"));
        directQuery.setParameter("aStatus", "Cancelled");
        directQuery.setParameter("aStatus2", "Open");

        TypedQuery<Task> directQuery2 = entityManager.createNamedQuery("Task.findByAliasAndStatus", Task.class);
        directQuery2.setParameter("aSerNum", new BigInteger("8"));
        directQuery2.setParameter("aStatus", "Cancelled");
        directQuery2.setParameter("aStatus2", "Open");

        TypedQuery<Task> directQuery3 = entityManager.createNamedQuery("Task.findByAliasAndStatus", Task.class);
        directQuery3.setParameter("aSerNum", new BigInteger("22"));
        directQuery3.setParameter("aStatus", "Cancelled");
        directQuery3.setParameter("aStatus2", "Open");

        TypedQuery<Task> directQuery4 = entityManager.createNamedQuery("Task.findByAliasAndStatus", Task.class);
        directQuery4.setParameter("aSerNum", new BigInteger("18"));
        directQuery4.setParameter("aStatus", "Cancelled");
        directQuery4.setParameter("aStatus2", "Open");

        TypedQuery<Task> directQuery5 = entityManager.createNamedQuery("Task.findByAliasAndStatus", Task.class);
        directQuery5.setParameter("aSerNum", new BigInteger("19"));
        directQuery5.setParameter("aStatus", "Cancelled");
        directQuery5.setParameter("aStatus2", "Open");

        List<Task> directList = directQuery.getResultList();
        directList.addAll(directQuery2.getResultList());
        directList.addAll(directQuery3.getResultList());
        directList.addAll(directQuery4.getResultList());
        directList.addAll(directQuery5.getResultList());

        int tasksAdded = 0;

        for(Task a : directList){
            Patient ifExists = isInPatientData(a.getPatientSerNum());

            if(ifExists != null){
                ifExists.addDataPoint(new DataPoint(a.getCreationDate(), DataPointType.TASK));
                tasksAdded++;
            }
        }

        if(setDebug) System.out.println("Tasks added: "+tasksAdded);
    }

    private void addDocuments(){
        TypedQuery<Document> directQuery = entityManager.createNamedQuery("Document.findByAlias", Document.class);

        List<Document> directList = directQuery.getResultList();

        int docsAdded = 0;

        for(Document d : directList){
            Patient ifExists = isInPatientData(d.getPatientSerNum());

            if(ifExists != null){
                ifExists.addDataPoint(new DataPoint(d.getCreatedTimeStamp(), DataPointType.DOCUMENT));
                docsAdded++;
            }
        }

        if(setDebug) System.out.println("Docs added: "+docsAdded);
    }

    public Patient isInPatientData(int setNum){
        for(Patient p : workingSet){
            if(p.getPatientSerNum() == setNum){
                return p;
            }
        }
        return null;
    }

    public Set<Patient> getWorkingSet(){
        return workingSet;
    }

    private void setEntityManager(){
        entityManager = ((MainUI) UI.getCurrent()).getEntityManager();
    }
}
