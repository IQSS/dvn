/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mpieters
 */
public class AccessExpressionParser {

    private final static Logger LOGGER = Logger.getLogger(AccessExpressionParser.class.getPackage().getName());

    private String expString = null;
    private expressionNode expTree = null;

    public AccessExpressionParser() {
    }

    public AccessExpressionParser(String expression) {
        expString = expression;
        expTree = parse(expression, null);
    }
    
    public void setExpression(String expression) {
        expString = expression;
        expTree = parse(expression, null);        
    }

    private expressionNode parse(String expression, expressionNode parent) {
        expressionNode tree = null;

        if (expression != null && expression.length() > 0) {
            if (expression.contains("|")) {
                tree = new expressionNode(parent, expressionOperator.OR);
                StringTokenizer st = new StringTokenizer(expression, "|");
                while (st.hasMoreTokens()) {
                    String t = st.nextToken();
                    expressionNode x = parse(t, tree);
                    if (x != null) {
                        tree.add(x);
                    }
                }
            } else if (expression.contains("&")) {
                tree = new expressionNode(parent, expressionOperator.AND);
                StringTokenizer st = new StringTokenizer(expression, "&");
                while (st.hasMoreTokens()) {
                    String t = st.nextToken();
                    expressionNode x = parse(t, tree);
                    if (x != null) {
                        tree.add(x);
                    }
                }
            } else {
                tree = new expressionNode(parent, expression);
            }
        } else {
            tree = null;
        }
        return tree;
    }
    
    public Boolean evaluate(Map<String, String> data) {
        if (expTree != null) {
            LOGGER.log(Level.FINE, "Evaluating data");
            return expTree.evaluate(data);
        }
        return false;
    }

    /* 
     * Inner classes */
    public enum expressionOperator {

        AND,
        OR
    }

    public enum equalityMatch {

        EQUAL,
        UNEQUAL
    }

    private class Condition {

        private String left;
        private String right;
        private equalityMatch match;

        public Condition(String expression) {
            String regex = "^(\\p{Alnum}+)(!?=)(.*)$";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(expression);
            if (m.matches() && m.groupCount() == 3) {
                left = m.group(1).toLowerCase();
                right = m.group(3);
                String eqm = m.group(2);
                if ("!=".equals(eqm)) {
                    match = equalityMatch.UNEQUAL;
                } else if ("=".equals(eqm)) {
                    match = equalityMatch.EQUAL;
                }
            }
        }

        public Condition(String left, equalityMatch match, String right) {
            this.left = left.toLowerCase();
            this.match = match;
            this.right = right;
        }

        private Boolean evaluateList(List ldata) {
            Boolean result = false;
            if (ldata != null) {
                Iterator i = ldata.iterator();
                while (!result && i.hasNext()) {
                    String sdata = (String) i.next();
                    result |= evaluateString(sdata);
                }
            }
            return result;
        }
        
        private Boolean evaluateString(String sdata) {
            Boolean result = false;
                switch (match) {
                    case EQUAL:
                        result = (sdata != null) && ("*".equals(right) || sdata.equalsIgnoreCase(right));
                        break;
                    case UNEQUAL:
                        result = (sdata == null) || (!("*".equals(right) || sdata.equalsIgnoreCase(right)));
                        break;
                }
                return result;
        }
        
        public Boolean evaluate(Map<String, String> data) {
            if (left != null) {
                Object v = data.get(left);
                Boolean result = false;
                if (v instanceof String) {
                    LOGGER.log(Level.INFO, "Evaluating String value {0}", v);
                    result = evaluateString((String) v);
                } else if (v instanceof List) {
                    LOGGER.log(Level.INFO, "Evaluating List value {0}", v.toString());
                    result = evaluateList((List) v);            
                }
                LOGGER.log(Level.INFO, "Evaluating " + this.toString() + ": {0}", result);
                return result;
            }
            return false;
        }

        @Override
        public String toString() {
            String matchExpr = (match == equalityMatch.EQUAL) ? "=" : "!=";
            return ((left != null) ? left : "") + ((matchExpr != null) ? matchExpr.toString() : "?") + ((right != null) ? right : "");
        }
    }

    private class expressionNode extends ArrayList {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private expressionOperator op = null;
        private Condition condition = null;
        private expressionNode parent = null;

        public expressionNode(expressionNode parent, String condition) {
            this.parent = parent;
            this.condition = new Condition(condition);
        }

        public expressionNode(expressionNode parent, expressionOperator op) {
            this.parent = parent;
            this.op = op;
        }

        public Boolean evaluate(Map<String, String> data) {
            if (condition != null) {
                Boolean result = condition.evaluate(data);
                return result;
            } else if (op != null && size() > 0) {
                Boolean result;
                if (op == expressionOperator.AND) {
                    result = true;
                    for (Iterator<expressionNode> it = this.iterator(); it.hasNext();) {
                        result &= it.next().evaluate(data);
                    }
                } else {
                    result = false;
                    for (Iterator<expressionNode> it = this.iterator(); it.hasNext();) {
                        result |= it.next().evaluate(data);
                    }
                    
                }
                return result;
            }
            return false;
        }
    }
}
