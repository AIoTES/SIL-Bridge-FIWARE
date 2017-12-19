package eu.interiot.translators.syntax.SEAMS2;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import eu.interiot.translators.syntax.IllegalSyntaxException;
import eu.interiot.translators.syntax.SyntacticTranslator;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * INTER-IoT. Interoperability of IoT Platforms.
 * INTER-IoT is a R&D project which has received funding from the European
 * Union<92>s Horizon 2020 research and innovation programme under grant
 * agreement No 687283.
 * <p>
 * Copyright (C) 2016-2018, by (Author's company of this file):
 * - Systems Research Institute Polish Academy of Sciences
 * <p>
 * <p>
 * For more information, contact:
 * - @author <a href="mailto:mllorente@prodevelop.es">Miguel Ángel Llorente Carmona</a>, <a href="mailto:pawel.szmeja@ibspan.waw.pl">Paweł Szmeja</a>
 * - Project coordinator:  <a href="mailto:coordinator@inter-iot.eu"></a>
 * <p>
 * <p>
 * This code is licensed under the EPL license, available at the root
 * application directory.
 */

public class SEAMS2TranslatorV1 extends SyntacticTranslator<String> {

    public static String S2baseURI = "http://inter-iot.eu/syntax/SEAMS2#";
    public static String S2formatName = "SEAMS2v1";
    /**
     * Full list of attribute fields in Noatum
     */

    String[] attributeFields = new String[]{"GpsX", "GpsY", "GpsHDOP", "GpsSQ", "GpsDU", "Gps", "GpsV", "Dist",
            "GpsR", "TwO", "TwC", "TrLane", "Nmov", "Sp20", "Sp40", "SpTwin20", "SpTwin40", "SpTwin4x20", "FlippUp",
            "FlippDown", "NetLoad", "ETrolley", "JoyTr", "TrSea", "TrLand", "TrPark", "EHoist", "JoyHo", "HoistU",
            "HoistD", "BoDls", "BoUlt", "BoDw", "BoUp", "Bo45", "JoyGr", "GantR", "GantL", "Wheel", "RWheel", "PlcOn",
            "DrOn", "SprOn", "PrtOn", "CabSen", "CtCab", "CtGrn", "CtBoo", "CtEro", "MMant", "HookMo", "ManMo", "On",
            "EcoON", "StbyON", "Off", "OnR", "EcoOnR", "StbyOnR", "OffR", "Fault", "Alarm", "Warning", "WHours",
            "PwCons", "PwGen", "WindS", "RpmE", "ETorq", "FRate", "ASpeed", "TCool", "TOil", "TOilT", "VBat", "FLevel",
            "BoostP", "OilP", "HP1", "HP2", "HBrake", "Oper", "AdBL", "DM", "PPos1", "PPos2", "Reserva12", "Reserva11",
            "Reserva10", "Reserva9", "Reserva8", "Reserva7", "Reserva6", "Reserva5", "Reserva4", "Reserva3", "Reserva2",
            "Reserva1"};
    String[] attributeFieldsURIs = new String[]{"GpsXuri", "GpsYuri", "GpsHDOPuri", "GpsSQuri", "GpsDUuri", "Gpsuri",
            "GpsVuri", "Disturi", "GpsRuri", "TwOuri", "TwCuri", "TrLaneuri", "Nmovuri", "Sp20uri", "Sp40uri",
            "SpTwin20uri", "SpTwin40uri", "SpTwin4x20uri", "FlippUpuri", "FlippDownuri", "NetLoaduri", "ETrolleyuri",
            "JoyTruri", "TrSeauri", "TrLanduri", "TrParkuri", "EHoisturi", "JoyHouri", "HoistUuri", "HoistDuri",
            "BoDlsuri", "BoUlturi", "BoDwuri", "BoUpuri", "Bo45uri", "JoyGruri", "GantRuri", "GantLuri", "Wheeluri",
            "RWheeluri", "PlcOnuri", "DrOnuri", "SprOnuri", "PrtOnuri", "CabSenuri", "CtCaburi", "CtGrnuri", "CtBoouri",
            "CtErouri", "MManturi", "HookMouri", "ManMouri", "Onuri", "EcoONuri", "StbyONuri", "Offuri", "OnRuri",
            "EcoOnRuri", "StbyOnRuri", "OffRuri", "Faulturi", "Alarmuri", "Warninguri", "WHoursuri", "PwConsuri",
            "PwGenuri", "WindSuri", "RpmEuri", "ETorquri", "FRateuri", "ASpeeduri", "TCooluri", "TOiluri", "TOilTuri",
            "VBaturi", "FLeveluri", "BoostPuri", "OilPuri", "HP1uri", "HP2uri", "HBrakeuri", "Operuri", "AdBLuri",
            "DMuri", "PPos1uri", "PPos2uri", "Reserva12uri", "Reserva11uri", "Reserva10uri", "Reserva9uri",
            "Reserva8uri", "Reserva7uri", "Reserva6uri", "Reserva5uri", "Reserva4uri", "Reserva3uri", "Reserva2uri",
            "Reserva1"};
    String[] propertyFieldsNames = new String[]{"hasGpsX", "hasGpsY", "hasGpsHDOP", "hasGpsSQ", "hasGpsDU",
            "hasGps", "hasGpsV", "hasDist", "hasGpsR", "hasTwO", "hasTwC", "hasTrLane", "hasNmov", "hasSp20", "hasSp40",
            "hasSpTwin20", "hasSpTwin40", "hasSpTwin4x20", "hasFlippUp", "hasFlippDown", "hasNetLoad", "hasETrolley",
            "hasJoyTr", "hasTrSea", "hasTrLand", "hasTrPark", "hasEHoist", "hasJoyHo", "hasHoistU", "hasHoistD",
            "hasBoDls", "hasBoUlt", "hasBoDw", "hasBoUp", "hasBo45", "hasJoyGr", "hasGantR", "hasGantL", "hasWheel",
            "hasRWheel", "hasPlcOn", "hasDrOn", "hasSprOn", "hasPrtOn", "hasCabSen", "hasCtCab", "hasCtGrn", "hasCtBoo",
            "hasCtEro", "hasMMant", "hasHookMo", "hasManMo", "hasOn", "hasEcoON", "hasStbyON", "hasOff", "hasOnR",
            "hasEcoOnR", "hasStbyOnR", "hasOffR", "hasFault", "hasAlarm", "hasWarning", "hasWHours", "hasPwCons",
            "hasPwGen", "hasWindS", "hasRpmE", "hasETorq", "hasFRate", "hasASpeed", "hasTCool", "hasTOil", "hasTOilT",
            "hasVBat", "hasFLevel", "hasBoostP", "hasOilP", "hasHP1", "hasHP2", "hasHBrake", "hasOper", "hasAdBL",
            "hasDM", "hasPPos1", "hasPPos2", "hasReserva12", "hasReserva11", "hasReserva10", "hasReserva9",
            "hasReserva8", "hasReserva7", "hasReserva6", "hasReserva5", "hasReserva4", "hasReserva3", "hasReserva2",
            "hasReserva1"};
    Property hasGpsXuri, hasGpsY, hasGpsHDOP, hasGpsSQ, hasGpsDU, hasGps, hasGpsV, hasDist, hasGpsR, hasTwO, hasTwC,
            hasTrLane, hasNmov, hasSp20, hasSp40, hasSpTwin20, hasSpTwin40, hasSpTwin4x20, hasFlippUp, hasFlippDown,
            hasNetLoad, hasETrolley, hasJoyTr, hasTrSea, hasTrLand, hasTrPark, hasEHoist, hasJoyHo, hasHoistU,
            hasHoistD, hasBoDls, hasBoUlt, hasBoDw, hasBoUp, hasBo45, hasJoyGr, hasGantR, hasGantL, hasWheel, hasRWheel,
            hasPlcOn, hasDrOn, hasSprOn, hasPrtOn, hasCabSen, hasCtCab, hasCtGrn, hasCtBoo, hasCtEro, hasMMant,
            hasHookMo, hasManMo, hasOn, hasEcoON, hasStbyON, hasOff, hasOnR, hasEcoOnR, hasStbyOnR, hasOffR, hasFault,
            hasAlarm, hasWarning, hasWHours, hasPwCons, hasPwGen, hasWindS, hasRpmE, hasETorq, hasFRate, hasASpeed,
            hasTCool, hasTOil, hasTOilT, hasVBat, hasFLevel, hasBoostP, hasOilP, hasHP1, hasHP2, hasHBrake, hasOper,
            hasAdBL, hasDM, hasPPos1, hasPPos2, hasReserva12, hasReserva11, hasReserva10, hasReserva9, hasReserva8,
            hasReserva7, hasReserva6, hasReserva5, hasReserva4, hasReserva3, hasReserva2, hasReserva1;
    Property[] propertyFields = new Property[]{hasGpsXuri, hasGpsY, hasGpsHDOP, hasGpsSQ, hasGpsDU, hasGps, hasGpsV, hasDist, hasGpsR, hasTwO, hasTwC,
            hasTrLane, hasNmov, hasSp20, hasSp40, hasSpTwin20, hasSpTwin40, hasSpTwin4x20, hasFlippUp, hasFlippDown,
            hasNetLoad, hasETrolley, hasJoyTr, hasTrSea, hasTrLand, hasTrPark, hasEHoist, hasJoyHo, hasHoistU,
            hasHoistD, hasBoDls, hasBoUlt, hasBoDw, hasBoUp, hasBo45, hasJoyGr, hasGantR, hasGantL, hasWheel, hasRWheel,
            hasPlcOn, hasDrOn, hasSprOn, hasPrtOn, hasCabSen, hasCtCab, hasCtGrn, hasCtBoo, hasCtEro, hasMMant,
            hasHookMo, hasManMo, hasOn, hasEcoON, hasStbyON, hasOff, hasOnR, hasEcoOnR, hasStbyOnR, hasOffR, hasFault,
            hasAlarm, hasWarning, hasWHours, hasPwCons, hasPwGen, hasWindS, hasRpmE, hasETorq, hasFRate, hasASpeed,
            hasTCool, hasTOil, hasTOilT, hasVBat, hasFLevel, hasBoostP, hasOilP, hasHP1, hasHP2, hasHBrake, hasOper,
            hasAdBL, hasDM, hasPPos1, hasPPos2, hasReserva12, hasReserva11, hasReserva10, hasReserva9, hasReserva8,
            hasReserva7, hasReserva6, hasReserva5, hasReserva4, hasReserva3, hasReserva2, hasReserva1};
    /**
     * List of URIs of the field identifiers.
     */

    //Classes
    private String
            seamsObjectTypeURI,
            seamsAttributesTypeURI;
    //Properties
    private String
            hasNameURI,
            hasIdURI,
            hasMsgTimestampURI,
            hasTypeURI,
            hasVersionURI,
            hasAttributesURI;
    /**
     * List of Jena Model properties/resources
     */

    private Resource
            seamsObjectType,
            seamsAttributesType;
    private Property
            hasId,
            hasType,
            hasVersion,
            hasMsgTimestamp,
            hasName,
            hasAttributes;
    private Map<String, Property> attributeFieldsMap;

    public SEAMS2TranslatorV1() {

        super(S2baseURI, S2formatName);

		/* URI assignations to the Apache Jena elements */

        setSeamsObjectTypeURI(getBaseURI() + "SEAMSObject");
        setSeamsAttributesTypeURI(getBaseURI() + "SEAMSAttributes");

        setHasNameURI(getBaseURI() + "hasName");
        setHasIdURI(getBaseURI() + "hasId");
        setHasMsgTimestampURI(getBaseURI() + "hasMsgTimestamp");
        setHasTypeURI(getBaseURI() + "hasType");
        setHasVersionURI(getBaseURI() + "hasVersion");
        setHasAttributesURI(getBaseURI() + "hasAttributes");

		/* Apache Jena model creation */

        Model jenaModel = ModelFactory.createDefaultModel();

		/* Attach the URIs to the created model */

        seamsObjectType = jenaModel.createResource(getSeamsObjectTypeURI());
        seamsAttributesType = jenaModel.createResource(getSeamsAttributesTypeURI());

        hasId = jenaModel.createProperty(getHasIdURI());
        hasType = jenaModel.createProperty(getHasTypeURI());
        hasVersion = jenaModel.createProperty(getHasVersionURI());
        hasMsgTimestamp = jenaModel.createProperty(getHasMsgTimestampURI());
        hasName = jenaModel.createProperty(getHasNameURI());
        hasAttributes = jenaModel.createProperty(getHasAttributesURI());

        initAttributeFields(jenaModel);

    }

    public String getSeamsObjectTypeURI() {
        return seamsObjectTypeURI;
    }

    public void setSeamsObjectTypeURI(String seamsObjectTypeURI) {
        this.seamsObjectTypeURI = seamsObjectTypeURI;
    }

    /*****
     ***
     * SEAMS2 ---> RDF
     ***
     *****/

    @Override
    public Model toJenaModel(String formatXString) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser parser = factory.createParser(formatXString);
        JsonNode topLevelNode = mapper.readTree(parser);

        Model jenaModel = ModelFactory.createDefaultModel();

        //TODO: Verify if SEAMS2 only produces messages with a single object in them (i.e. no arrays or "loose" values)
        if (topLevelNode.isObject()) {
            Resource mySEAMSObject = jenaModel.createResource();
            parseSEAMS2JSONObjectToJena(mySEAMSObject, topLevelNode, jenaModel);
        } else {
            throw new IllegalSyntaxException("SEAMS2 JSON messages need to have a top-level JSON object.");
        }

        return jenaModel;
    }

    private void parseSEAMS2JSONObjectToJena(Resource objectResource, JsonNode objectNode, Model jenaModel) throws IllegalSyntaxException {
        Iterator<Map.Entry<String, JsonNode>> it = objectNode.fields();
        boolean containsID = false;
//        "msgTimestamp": "20/09/2017 13:28:08",
//                "name": "RTG040",
//                "id": 48,
//                "type": null,
//                "version": "1.0",
//                "attributes": {

        while (it.hasNext()) {
            Map.Entry<String, JsonNode> field = it.next();
            if (field.getKey().equals("id")) {
                // Entity has to have an id
                // mllorente if no id is provided --> EXIT (validate schema)
                containsID = true;
                objectResource.addProperty(RDF.type, seamsObjectType);
                objectResource.addLiteral(hasId, field.getValue().asInt());
            } else if (field.getKey().equals("type")) {
                //TODO: Check if null
                objectResource.addLiteral(hasType, field.getValue().asText());
            } else if (field.getKey().equals("name")) {
                objectResource.addLiteral(hasName, field.getValue().asText());
            } else if (field.getKey().equals("version")) {
                objectResource.addLiteral(hasVersion, field.getValue().asText());
            } else if (field.getKey().equals("msgTimestamp")) {
                objectResource.addLiteral(hasMsgTimestamp, field.getValue().asText());
            } else if (field.getKey().equals("attributes")) {
                Resource attributesResource = jenaModel.createResource();
                //Not sure about this - I think it should has type if it is not specified -- I comment it
                attributesResource.addProperty(RDF.type, seamsAttributesType);
                objectResource.addProperty(hasAttributes, attributesResource);
                parseSEAMS2JSONAttributesToJena(attributesResource, field.getValue(), jenaModel);
            }
        }

        // mllorente if no id is provided --> EXIT (validate schema)
        if (!containsID) {
            throw new IllegalSyntaxException("An 'id' attribute mnissing from a SEAMS2 JSON object (every SEAMS2 JSON object needs to have it).");
        }
    }

    private void parseSEAMS2JSONAttributesToJena(Resource objectResource, JsonNode attributesNode, Model jenaModel) throws IllegalSyntaxException {
        Iterator<Map.Entry<String, JsonNode>> it = attributesNode.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> field = it.next();
            if (attributeFieldsMap.containsKey(field.getKey())) {
                Iterator<Map.Entry<String, JsonNode>> attrIt = field.getValue().fields();
                while (attrIt.hasNext()) {
                    Map.Entry<String, JsonNode> attrField = attrIt.next();
                    //TODO: Is this the only attribute we parse?
                    if (attrField.getKey().equals("value")) {
                        parseValueToJena(objectResource, attributeFieldsMap.get(field.getKey()), attrField.getValue(), jenaModel);
//                        objectResource.addLiteral(attributeFieldsMap.get(field.getKey()), attrField.getValue().asText());
                    }
                }
            }
        }
    }

    private void parseValueToJena(Resource res, Property prop, JsonNode jsonNode, Model jenaModel) throws IllegalSyntaxException {
        //If its a data value
        if (jsonNode.isValueNode()) {
            if (jsonNode.isNumber()) {
                //Numeric value
                if (jsonNode.isInt()) {
                    res.addLiteral(prop, new Integer(jsonNode.asInt()));
                } else if (jsonNode.isLong()) {
                    res.addLiteral(prop, jsonNode.asLong());
                } else if (jsonNode.isFloat() || jsonNode.isFloatingPointNumber()) {
                    res.addLiteral(prop, new Float(jsonNode.asDouble()));
                } else {
                    res.addLiteral(prop, jsonNode.asDouble());
                }
            } else if (jsonNode.isBoolean()) {
                //Boolean value
                res.addLiteral(prop, jsonNode.asBoolean());
            } else if (jsonNode.isTextual()) {
                //Textual value
                res.addLiteral(prop, jsonNode.asText());
            } else {
                //Not-typed (string) value
                res.addProperty(prop, jsonNode.asText());
            }
//            res.addLiteral(hasValProperty, jsonNode.asText());
        } else if (jsonNode.isArray() || jsonNode.isObject()) {
            throw new IllegalSyntaxException("Encountered a non-value Json node under 'value' attribute of one of SEAMS2 attributes.");
        }
    }

    /*****
     ***
     * RDF ---> SEAMS2
     ***
     *****/

    @Override
    public String toFormatX(Model jenaModelParam) throws Exception {
        Model jenaModel = ModelFactory.createDefaultModel().add(jenaModelParam);
        ObjectMapper mapper = new ObjectMapper();
        LinkedList<JsonNode> jsonNodeList = new LinkedList<JsonNode>();

        //Find the top-level RDF Entity (the entity that does not appear in Object of RDF triples)
        Query topLevelQuery = QueryFactory.create("SELECT DISTINCT ?top WHERE { ?top ?y ?z MINUS {?a ?b ?top} }");
        QueryExecution topLevelQueryExec = QueryExecutionFactory.create(topLevelQuery, jenaModel);
        ResultSet results = topLevelQueryExec.execSelect();

        while (results.hasNext()) {
            QuerySolution solution = results.next();
            RDFNode resultNode = solution.get("top");
//            System.out.println(resultNode);

            JsonNode someTopLevelNode = parseRDFEntityToJson(resultNode.asResource(), jenaModel, mapper);

            if (someTopLevelNode != null) {
                jsonNodeList.add(someTopLevelNode);
            }
        }

        JsonNode topLevelNode = null;
        if (jsonNodeList.isEmpty()) {
            throw new IllegalSyntaxException("No top-level RDF entity found (a root node in RDF graph is required in SEAMS2 RDF)");
        } else if (jsonNodeList.size() == 1) {
            topLevelNode = jsonNodeList.getFirst();
        } else {
            throw new IllegalSyntaxException("Multiple top-level RDF entities found (SEAMS2 RDF must have only one root node in RDF graph)");
        }

        return topLevelNode.toString();
    }

    private JsonNode parseRDFEntityToJson(Resource entityResource, Model jenaModel, ObjectMapper mapper) {
        //Check entity type
        StmtIterator typesIt = jenaModel.listStatements(new SimpleSelector(entityResource, RDF.type, (RDFNode) null));
        while (typesIt.hasNext()) {
            Statement stmt = typesIt.next();
            Resource type = stmt.getObject().asResource();

            if (type.equals(seamsObjectType)) {
                ObjectNode jsonNode = mapper.createObjectNode();
//                typesIt.remove();
                parseSEAMSObjectResourceToJson(entityResource, jsonNode, jenaModel, mapper);
                return jsonNode;

            }
        }
        return null;
    }

    private void parseSEAMSObjectResourceToJson(Resource entityResource, ObjectNode entity, Model jenaModel, ObjectMapper mapper) {

        //TODO: Test this with Noatum format
        //TODO: Parse datatypes properly (i.e. not all as strings)

        //Parse id
        NodeIterator nodeIterator = jenaModel.listObjectsOfProperty(entityResource, hasId);
        if (nodeIterator.hasNext()) {
            RDFNode node = nodeIterator.next();
            String s = node.toString();
            if (node.isLiteral())
                s = node.asLiteral().getValue().toString();
            entity.put("id", s);
        }

        //Parse type
        nodeIterator = jenaModel.listObjectsOfProperty(entityResource, hasType);
        if (nodeIterator.hasNext()) {
            entity.put("type", nodeIterator.next().toString());
        }

        //Parse name
        nodeIterator = jenaModel.listObjectsOfProperty(entityResource, hasName);
        if (nodeIterator.hasNext()) {
            entity.put("name", nodeIterator.next().toString());
        }

        //Parse version
        nodeIterator = jenaModel.listObjectsOfProperty(entityResource, hasVersion);
        if (nodeIterator.hasNext()) {
            entity.put("version", nodeIterator.next().toString());
        }

        //Parse timestamp
        nodeIterator = jenaModel.listObjectsOfProperty(entityResource, hasMsgTimestamp);
        if (nodeIterator.hasNext()) {
            entity.put("msgTimestamp", nodeIterator.next().toString());
        }

        //Parse attributes
        nodeIterator = jenaModel.listObjectsOfProperty(entityResource, hasAttributes);
        if (nodeIterator.hasNext()) {
            ObjectNode attributesNode = mapper.createObjectNode();
            entity.set("attributes", attributesNode);
            parseAttributesObjectToJson(nodeIterator.next().asResource(), attributesNode, jenaModel, mapper);
        }
    }

    private void parseAttributesObjectToJson(Resource attributesResource, ObjectNode attributesNode, Model jenaModel, ObjectMapper mapper) {
        for (int i = 0; i < attributeFields.length; i++) {
            //Parse field
            NodeIterator nodeIterator = jenaModel.listObjectsOfProperty(attributesResource, propertyFields[i]);
            if (nodeIterator.hasNext()) {
                RDFNode attributeField = nodeIterator.next();

                ObjectNode newAttributeNode = mapper.createObjectNode();

                attributesNode.set(attributeFields[i], newAttributeNode);

                ValueNode valNode = null;

                if (attributeField.isLiteral()) {
                    valNode = parseLiteralToValueNode(attributeField.asLiteral(), mapper);
                } else if (attributeField.isResource()) {
                    if (attributeField.isAnon()) {
                        valNode = mapper.getNodeFactory().textNode(attributeField.asResource().getId().toString());
                    } else {
                        valNode = mapper.getNodeFactory().textNode(attributeField.asResource().getURI());
                    }
                }
                newAttributeNode.set("value", valNode);
            }
        }
    }

    private ValueNode parseLiteralToValueNode(Literal literal, ObjectMapper mapper) {
        //TODO: Maybe add more datatypes

        RDFDatatype datatype = literal.getDatatype();
        if (datatype != null) {
            if (datatype.equals(XSDDatatype.XSDboolean)) {
                return mapper.getNodeFactory().booleanNode(literal.getBoolean());
            }

            if (datatype.equals(XSDDatatype.XSDint) || datatype.equals(XSDDatatype.XSDinteger)) {
                return mapper.getNodeFactory().numberNode(literal.getInt());
            }

            if (datatype.equals(XSDDatatype.XSDlong)) {
                return mapper.getNodeFactory().numberNode(literal.getLong());
            }

            if (datatype.equals(XSDDatatype.XSDfloat)) {
                return mapper.getNodeFactory().numberNode(literal.getFloat());
            }

            if (datatype.equals(XSDDatatype.XSDdouble)) {
                return mapper.getNodeFactory().numberNode(literal.getDouble());
            }
        }

        return mapper.getNodeFactory().textNode(literal.getValue().toString());
    }

    /**
     * Takes in a JSON string and attempts to make it pretties by adding spaces, indentation and new lines
     *
     * @param jsonString
     * @return
     * @throws IOException
     */
    public String prettifyJsonString(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser parser = factory.createParser(jsonString);
        JsonNode jsonNode = mapper.readTree(parser);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
    }

    public void initAttributeFields(Model jenaModel) {
        attributeFieldsMap = new HashMap<String, Property>();
        for (int i = 0; i < attributeFields.length; i++) {
            attributeFieldsURIs[i] = getBaseURI() + propertyFieldsNames[i];
            propertyFields[i] = jenaModel.createProperty(attributeFieldsURIs[i]);
            attributeFieldsMap.put(attributeFields[i], propertyFields[i]);
        }
    }


    public String getHasNameURI() {
        return hasNameURI;
    }

    public void setHasNameURI(String hasNameURI) {
        this.hasNameURI = hasNameURI;
    }

    public String getHasIdURI() {
        return hasIdURI;
    }

    public void setHasIdURI(String hasIdURI) {
        this.hasIdURI = hasIdURI;
    }

    public String getHasMsgTimestampURI() {
        return hasMsgTimestampURI;
    }

    public void setHasMsgTimestampURI(String hasMsgTimestampURI) {
        this.hasMsgTimestampURI = hasMsgTimestampURI;
    }

    public String getHasTypeURI() {
        return hasTypeURI;
    }

    public void setHasTypeURI(String hasTypeURI) {
        this.hasTypeURI = hasTypeURI;
    }

    public String getHasVersionURI() {
        return hasVersionURI;
    }

    public void setHasVersionURI(String hasVersionURI) {
        this.hasVersionURI = hasVersionURI;
    }

    public String getHasAttributesURI() {
        return hasAttributesURI;
    }

    public void setHasAttributesURI(String hasAttributesURI) {
        this.hasAttributesURI = hasAttributesURI;
    }

    public String getSeamsAttributesTypeURI() {
        return seamsAttributesTypeURI;
    }

    public void setSeamsAttributesTypeURI(String seamsAttributesTypeURI) {
        this.seamsAttributesTypeURI = seamsAttributesTypeURI;
    }
}
