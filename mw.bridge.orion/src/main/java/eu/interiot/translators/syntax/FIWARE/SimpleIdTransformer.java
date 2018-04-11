package eu.interiot.translators.syntax.FIWARE;

import eu.interiot.translators.syntax.transformer.JenaTransformer;
import org.apache.jena.rdf.model.*;

import java.util.Set;


public class SimpleIdTransformer extends JenaTransformer {


    private String baseURI = "http://inter-iot.eu/syntax/FIWAREv2#";
    private String hasIdURI = baseURI + "hasId";

    public SimpleIdTransformer() {
    	
    }
    
    @Override
    public Model transformTowardsFormatX(Model input) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public Model transformTowardsINTERMW(Model input) {
        Model model = ModelFactory.createDefaultModel().add(input);
      
        Property hasId = model.createProperty(hasIdURI);
        Resource newId = null;
        Resource currentGeneratedId = null;
        Set<Statement> idStatements = model.listStatements(new SimpleSelector(null, hasId, null, null)).toSet();
        for (Statement idStmt : idStatements) {
        	newId = model.createResource((isURI(idStmt.getObject().asLiteral().toString()) ? "" : "http://") + idStmt.getObject().asLiteral().toString());
        	currentGeneratedId = idStmt.getSubject();
        }
        
        Set<Statement> currentIdStatements = model.listStatements(new SimpleSelector(currentGeneratedId, null, null, null)).toSet();
        for (Statement idStmt : currentIdStatements) {
            model.add(newId, idStmt.getPredicate(), idStmt.getObject());
            model.remove(idStmt);
        }
        return model;
    }

    private boolean isURI(String uri) {
        return uri.startsWith("http://") && uri.length() > 7;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }
}
