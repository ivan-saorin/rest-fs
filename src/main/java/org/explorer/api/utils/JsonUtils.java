package org.explorer.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.explorer.api.exceptions.WebApiRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtils {

	private static JsonUtils instance = new JsonUtils();
	
	private static ObjectMapper mapper;

    private static ObjectMapper getMapper()
    {
        if (mapper == null)
        {
            mapper = new ObjectMapper();
        }

        return mapper;
    }
	
	private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	public class NodeCastBuilder {
		
		private JsonNode node;
		
		public NodeCastBuilder(JsonNode node) {
			this.node = node;
		}
		
		public ObjectNode asObject() throws ClassCastException {
			return ObjectNode.class.cast(node);
		}

		public ArrayNode asArray() throws ClassCastException {
			return ArrayNode.class.cast(node);
		}
		
	}
	
	public static NodeCastBuilder jsonNode(JsonNode node) {
		return instance.new NodeCastBuilder(node);
	}	
	
	public static JsonNode getPropertyStatus(JsonNode node, String property, Object... additional) {
		String strAdditional = ((additional != null) && (additional.length > 0)) ? Arrays.toString(additional) : "";
		JsonNode propertyNode = node.path(property);
		if (propertyNode.isMissingNode()) {
			logger.info("NO property [{}] found. {}", property, strAdditional);
			
			if (additional.length > 0) {
				for (int i = 0; i < additional.length; i++) {
					JsonNode n = null;
					Object obj = additional[i];
					if ((additional.length - i) > 0) {
						Object[] objs = Arrays.copyOfRange(additional, i+1, additional.length);
						n = getPropertyStatus(node, obj.toString(), objs);
					}
					else {
						n = getPropertyStatus(node, obj.toString());
					}
					if (!n.isMissingNode()) {
						return n;
					}
				}
			}
		}
		else if (propertyNode.isArray()) {
			logger.debug("Found property [{}] of type {} with size of {}. {}", property, propertyNode.getNodeType(), propertyNode.size(), strAdditional);
		}
		else if (propertyNode.isNull()) {
			logger.debug("Found property [{}] of type {} with value [null]. {}", property, propertyNode.getNodeType(), strAdditional);
		}
		else if (propertyNode.isTextual() || propertyNode.isNumber()) {
			logger.debug("Found property [{}] of type {} with value [{}]. {}", property, propertyNode.getNodeType(), propertyNode.asText().replaceAll("(\\r|\\n|\\t)", ""), strAdditional);
		}
		else {
			logger.debug("Found property [{}] of type {}. {}", property, propertyNode.getNodeType(), strAdditional);
		}
		return propertyNode;
	}
	
	public static String toString(JsonNode node, boolean prettyPrint) {
		try {
			//String s;
			if (prettyPrint) {
				getMapper().enable(SerializationFeature.INDENT_OUTPUT);
			}
			String str = getMapper().writeValueAsString(node);
			if (!prettyPrint) {
				str = str.replaceAll("(\\r|\\n|\\t)", "");
			}
			return str;
		} catch (JsonProcessingException e) {
			throw new WebApiRuntimeException(e);
		}
	}
	
	public static void debug(String title, JsonNode node, boolean prettyPrint) {
		String s = toString(node, prettyPrint);
		logger.debug(title + " " + s);
	}
    
    
	public static JsonNode findLastNonMissing(JsonNode root, String expr) {
		String[] parts = expr.split("/");
		parts = removeInvalid(parts);
		String newExpr = join(parts, "/");
		JsonNode result = root.at(newExpr);
		while (result.isMissingNode() || (result.equals(root))) {
			if (parts.length == 0) {
				return result;
			}
			parts = removeLast(parts);
			newExpr = join(parts, "/");
			result = root.at(newExpr);
		}
		return result;
	}
    
	private static String[] removeLast(String[] parts) {
		return Arrays.copyOfRange(parts, 0, parts.length - 1);
	}

	private static String join(String[] parts, String separator) {
		StringBuffer sb = new StringBuffer();
		for (String part : parts) {
			sb.append(separator).append(part);
		}
		return sb.toString();
	}

	private static String[] removeInvalid(String[] parts) {
		ArrayList<String> parts2 = new ArrayList<>();
		for (String part : parts) {
			if ((part != null) && (!"/".equals(part)) && (!"".equals(part))) {
				parts2.add(part);
			}
		}
		String[] result = new String[parts2.size()]; 
		result = parts2.toArray(result);
		return result;
	}

	public static JsonNode fromString(String json) {
		return makeJson(json);
	}
	
	public static JsonNode fromResource(String testResource) throws JsonProcessingException, IOException {
		InputStream in = JsonUtils.class.getResourceAsStream(testResource);
		JsonNode node = getMapper().readTree(in);
		return node;
	}	

	public static ObjectNode ensureObjectNode(JsonNode parent, String nodeName) {
		JsonNode bindingNode = parent.get(nodeName);
		if (bindingNode != null) {
			ObjectNode casted = Utils.getInstance().object(bindingNode).as(ObjectNode.class);
			return casted;
		}
		
		if (bindingNode == null) {
			ObjectMapper mapper = new ObjectMapper();
			bindingNode = mapper.createObjectNode();
			ObjectNode casted = Utils.getInstance().object(parent).as(ObjectNode.class);
			casted.set(nodeName, bindingNode);
		}
		return (ObjectNode) bindingNode;
	}
	
	public static ArrayNode ensureArrayNode(JsonNode parent, String nodeName) {
		JsonNode bindingNode = parent.get(nodeName);
		if (bindingNode != null) {
			ArrayNode casted = Utils.getInstance().object(bindingNode).as(ArrayNode.class);
			return casted;
		}
		
		if (bindingNode == null) {
			ObjectMapper mapper = new ObjectMapper();
			bindingNode = mapper.createArrayNode();
			ObjectNode casted = Utils.getInstance().object(parent).as(ObjectNode.class);
			casted.set(nodeName, bindingNode);
		}
		return (ArrayNode) bindingNode;
	}
	
	public Map<String, String> jsonObjectToMap(JsonNode objectNode) {
		HashMap<String, String> map = new HashMap<>();
		Iterator<String> fieldnames = objectNode.fieldNames();
		while(fieldnames.hasNext()) {
			String fieldname = fieldnames.next();
			String value = objectNode.get(fieldname).asText();
			map.put(fieldname, value);			
		}
		return map;
	}

    public static JsonNode get(JsonNode node, String property)
    {
        JsonNode val = node.get(property);
        if (val == null)
        {
            throw new IllegalArgumentException("Property " + property
                    + " is not defined in " + node);
        }
        return val;
    }

    public static String getText(JsonNode node, String property, String defaultValue)
    {
        if (!node.has(property))
            return defaultValue;
        return get(node, property).textValue();
    }

    public static String getText(JsonNode node, String property)
    {
        return get(node, property).textValue();
    }

    public static String[] asArray(JsonNode parent, String property)
    {
        if (!parent.has(property) || parent.get(property).isNull())
            return null;

        return asArray(get(parent, property));
    }

    public static String[] asArray(JsonNode node)
    {
        if (node == null)
            throw new IllegalArgumentException("Specified JsonNode is null");

        if (node.isArray())
        {
            ArrayNode anode = (ArrayNode) node;
            int nelements = anode.size();
            String[] array = new String[nelements];
            for (int i = 0; i < nelements; i++)
            {
                array[i] = anode.get(i).textValue();
            }
            return array;
        }
        else
        {
            return new String[] { node.textValue() };
        }
    }

    public static Object asObject(JsonNode node)
    {
        if (node.isTextual())
            return node.textValue();
        else if (node.isInt())
            return node.intValue();
        else if (node.isFloatingPointNumber())
            return node.doubleValue();
        else if (node.isBoolean())
            return node.booleanValue();

        return null;
    }

    public static ObjectNode createObjectNode() {
    	return getMapper().createObjectNode();
    }
    
    public static ObjectNode createObjectNode(Object... keyvals)
    {
        ObjectNode node = getMapper().createObjectNode();
        for (int i = 0; i < keyvals.length; i += 2)
        {
            if (keyvals[i + 1] instanceof String)
            {
                node.put((String) keyvals[i], (String) keyvals[i + 1]);
            }
            else if (keyvals[i + 1] instanceof Integer)
            {
                int val = (Integer) keyvals[i + 1];
                node.put((String) keyvals[i], val);
            }
            else if (keyvals[i + 1] instanceof Boolean)
            {
                boolean val = (Boolean) keyvals[i + 1];
                node.put((String) keyvals[i], val);
            }
            else
            {
                node.set((String) keyvals[i], (JsonNode) keyvals[i + 1]);
            }
        }

        return node;
    }

    public static ArrayNode createArrayNode(String item)
    {
        return createArrayNode(new String[] { item });
    }

    public static ArrayNode createArrayNode()
    {
        return getMapper().createArrayNode();
    }

    public static ArrayNode createArrayNode(JsonNode... elementNodes)
    {
        ArrayNode result = getMapper().createArrayNode();
        for (int i = 0; i < elementNodes.length; i++)
            result.add(elementNodes[i]);
        return result;
    }

    public static ArrayNode createArrayNode(String[] items)
    {
        ArrayNode anode = getMapper().createArrayNode();
        for (String item : items)
        {
            anode.add(item);
        }
        return anode;
    }

    public static int getIndexFromArray(ArrayNode anode, JsonNode element)
    {
        for (int i = 0; i < anode.size(); i++)
        {
            if (anode.get(i) == element)
                return i;
        }
        return -1;
    }

    public static void deleteFromArrayNode(ArrayNode parentNode, ObjectNode operatorNode)
    {
        int index = getIndexFromArray((ArrayNode) parentNode, operatorNode);
        ((ArrayNode) parentNode).remove(index);
    }

    private static JsonNode makeJson(String str)
    {
    	if (str == null) {
    		return null;
    	}
        str = str.replace('\'', '"');
        try
        {
            return mapper.readValue(str, JsonNode.class);
        }
        catch (JsonParseException e)
        {
            int idx = (int) e.getLocation().getCharOffset();
            System.out.println(str);
            if (idx >= str.length())
            {
                idx = 1;
            }
            String substr = str.substring(0, idx);
            System.out.println(substr);
        }
        catch (JsonMappingException e)
        {
        }
        catch (IOException e)
        {

        }
        catch (Throwable e)
        {

        }
        
        //throw new RuntimeException();
        return null;
    }

    public static ArrayList<JsonNode> toArrayList(ArrayNode arrayNode)
    {
        ArrayList<JsonNode> result = new ArrayList<JsonNode>();
        for (JsonNode elem : arrayNode)
            result.add(elem);
        return result;
    }

    public static ArrayNode toArrayNode(List<JsonNode> nodelist)
    {
        ArrayNode result = createArrayNode();
        for (JsonNode elem : nodelist)
            result.add(elem);
        return result;
    }

    public static JsonNode cloneNode(JsonNode json)
    {
        String str = json.toString();
        try
        {
            return getMapper().readValue(str, JsonNode.class);
        }
        catch (JsonParseException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (JsonMappingException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

      public static String encodePath(JsonNode path)
    {
        if (path.isTextual())
            return path.textValue();
        else
        {
            String root = getText(path, "root");
            int startDate = Integer.parseInt(getText(path, "startDate"));
            int endDate = Integer.parseInt(getText(path, "endDate"));
            if (path.get("origStartDate") != null)
              return String.format("%s#START%d#END%d#ORIGSTART%d", root, startDate, endDate, Integer.parseInt(getText(path, "origStartDate")));
            return String.format("%s#START%d#END%d", root, startDate, endDate);
        }
    }

    public static JsonNode decodePath(String pathStr)
    {
        ObjectNode node = getMapper().createObjectNode();

        if (pathStr.contains("#START"))
        {
            String root = pathStr.split("#START")[0];
            String[] startEnd;
            int origStartDate = -1;
            int startDate, endDate;
            if (!pathStr.contains("#ORIGSTART")){
                startEnd= pathStr.split("#START")[1].split("#END");
                startDate = Integer.parseInt(startEnd[0]);
                endDate = Integer.parseInt(startEnd[1]);
            }
            else {
              startDate = Integer.parseInt(pathStr.split("#START")[1].split("#END")[0]);
              endDate = Integer.parseInt(pathStr.split("#START")[1].split("#END")[1].split("#ORIGSTART")[0]);
              origStartDate =  Integer.parseInt(pathStr.split("#START")[1].split("#END")[1].split("#ORIGSTART")[1]);
            }

            node.put("root", root);
            node.put("startDate", startDate);
            node.put("endDate", endDate);
            if (origStartDate != -1)
              node.put("origStartDate", origStartDate);
            return node;
        }
        else 
        {
            node.put("tmp", pathStr);
            return node.get("tmp");
        }
    }

    public static boolean isPrefixArray(ArrayNode pnode, ArrayNode snode)
    {
        if (pnode == null || snode == null || pnode.size() > snode.size())
            return false;

        for (int i = 0; i < pnode.size(); i++)
        {
            if (!pnode.get(i).textValue().equals(snode.get(i).textValue()))
                return false;
        }
        return true;
    }

    public static Object decodeConstant(JsonNode valNode, String valType)
    {
        if (valType != null && valNode.isNumber())
        {
            NumberType numType;
            Number numVal = valNode.numberValue();

            if (valType != null)
            {
                numType = NumberType.valueOf(valType.toUpperCase());
                switch (numType)
                {
                case DOUBLE:
                    return numVal.doubleValue();
                case FLOAT:
                    return numVal.floatValue();
                case INT:
                    return numVal.intValue();
                case LONG:
                    return numVal.longValue();
                default:
                    break;

                }
            }
        }

        return asObject(valNode);
    }

    public static void prettyPrint(String json) throws JsonGenerationException,
            JsonMappingException,
            IOException
    {
        System.out.println(new ObjectMapper().writer().writeValueAsString(json));
    }

}
