/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.z3950;

import java.util.logging.Logger;
import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;
import org.jzkit.search.util.QueryModel.Internal.ComplexNode;
import org.jzkit.search.util.QueryModel.Internal.InternalModelNamespaceNode;
import org.jzkit.search.util.QueryModel.Internal.InternalModelRootNode;
import org.jzkit.search.util.QueryModel.Internal.QueryNode;

/**
 *
 * @author roberttreacy
 */
public class LuceneQueryVisitor {
    private static Logger log = Logger.getLogger(Z3950search.class.getName());
    private String default_attrset = null;
    private SearchTerm searchTerm = null;

    public void visit(QueryNode qn) throws java.io.IOException {
      visit(qn,null);
    }

    public void visit(QueryNode qn, String default_ns) throws java.io.IOException {
        if ( qn instanceof InternalModelRootNode )
            visit((InternalModelRootNode)qn, default_ns);
        else if ( qn instanceof ComplexNode )
            visit((ComplexNode)qn, default_ns);
        else if ( qn instanceof AttrPlusTermNode )
            visit((AttrPlusTermNode)qn, default_ns);
        else if ( qn instanceof InternalModelNamespaceNode )
            visit((InternalModelNamespaceNode)qn, default_ns);
    }

    public void visit(InternalModelRootNode rn, String default_ns) throws java.io.IOException {
        log.info("Visit Root Node for: "+rn.toString());
        visit(rn.getChild(), default_ns);
    }

    public void visit(InternalModelNamespaceNode ns, String default_ns) throws java.io.IOException {
        // Hmm.. This is not good enough any more.. Namespace can change at different positions
        // in the query tree.
        log.info("Visit Internal Model Namespace Node for: " + ns.toString());
        default_attrset = ns.getAttrset();
        visit(ns.getChild(), default_attrset);
    }


    public void visit(ComplexNode cn, String default_ns) throws java.io.IOException {
        // System.err.println("SQLQueryVisitor::visit(ComplexNode)");

        log.info("Visit Complex Node for: " + cn.toString());
/*        BaseWhereCondition new_condition = null;
        Restrictable parent = (Restrictable) branch_stack.peek();

        int inumleft = cn.getLHS().countChildrenWithTerms();
        int inumright = cn.getRHS().countChildrenWithTerms();

        if ( ( inumleft > 0 ) &&
             ( inumright > 0 ) )
        {

            switch( cn.getOp() )
            {
                case 1:
                    new_condition = new ConditionCombination("AND");
                    parent.addCondition( new_condition );
                    branch_stack.push(new_condition);
                    break;
                case 2:
                    new_condition = new ConditionCombination("OR");
                    parent.addCondition( new_condition );
                    branch_stack.push(new_condition);
                    break;
                case 3:  // and not
                    new_condition = new ConditionCombination("AND NOT");
                    parent.addCondition( new_condition );
                    branch_stack.push(new_condition);
                    break;
                case 4:
                    break;
                default:
                    break;
            }

        }

        if ( inumleft > 0 )
        {
            visit(cn.getLHS(), default_ns);
        }

        if ( inumright > 0 )
        {
            visit(cn.getRHS(), default_ns);
        }

        if ( ( inumleft > 0 ) && ( inumright > 0 ) ) {
          if ( branch_stack.size() > 0 ) {
            branch_stack.pop();
          }
          else {
            log.warn("SQL Generation : Expected branch stack to be > size 0, not == 0");
          }
        } */
    }

    public void visit(AttrPlusTermNode aptn, String default_ns) throws java.io.IOException {
        log.info("Visit Attr Plus Term Node for: " + aptn.toString());
        if (aptn.getTerm() instanceof String) {
            searchTerm = new SearchTerm();
            searchTerm.setFieldName("any");
            searchTerm.setValue(aptn.getTermAsString(false));
        }
      /*Object ap = aptn.getAccessPoint();
      String access_point = ( ( ap instanceof AttrValue ) ? ((AttrValue)ap).getWithDefaultNamespace(default_ns) : ap.toString() );
      AttrMapping[] mappings = am.lookupAttr(access_point);

      if ( mappings != null ) {

        Restrictable parent = (Restrictable) branch_stack.peek();

        Object term = aptn.getTerm();
        AttrValue completeness = (AttrValue) aptn.getCompleteness();
        AttrValue relation_attr = (AttrValue) aptn.getRelation();

        // If this use attribute maps on to more than one set of target access points, for example
        // "TitleOrAuthor" might map onto title or author attributes, then we need to or the resultant
        // conditional components.
        if ( mappings.length > 1 ) {
          BaseWhereCondition new_condition = new ConditionCombination("OR");
          parent.addCondition( new_condition );
          parent = (Restrictable) new_condition;
        }

        for ( int mi=0; mi<mappings.length; mi++ ) {

          // Create a new AND condition which can be used to and any extra filter critera, or will just be normalised out
          // of the structure
          Restrictable new_condition = new ConditionCombination("AND");
          parent.addCondition( (BaseWhereCondition) new_condition );
          branch_stack.push(new_condition);

          AttrMapping m = mappings[mi];

          String relation = EQUALS;
                                                                                                                                          
          if ( relation_attr != null )
            relation = relation_attr.toString();
  
          // In processing is a special case for now :(
          if ( relation.equalsIgnoreCase("IN") ) {
            processInRelation( new_condition, aptn, default_ns, m, term, completeness, relation_attr );
          }
          else {
            // If term is an array or collection type, make an and and repeatedly call processMapping for each
            // else just call the once.
            if ( term instanceof String[] ) {
              log.debug("Processing array :"+term.getClass().getName());
              String[] term_array = (String[]) term;
              for ( int i=0; i<term_array.length; i++ ) {
                log.debug("Processing array component "+term_array[i]);
                if ( ( term_array[i] != null ) && ( !term_array[i].equals("") ) )
                  processMapping( new_condition, aptn, default_ns, m, term_array[i], completeness, relation_attr );
              }
            }
            else {
              log.i("Processing scalar component "+m);
              processMapping( new_condition, aptn, default_ns, m, term, completeness, relation_attr );
            }
          }

          branch_stack.pop();
        }
      }
      else {
        throw new UnknownAccessPointException("Unknown access point : "+access_point);
      } */
    }

    public SearchTerm getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(SearchTerm searchTerm) {
        this.searchTerm = searchTerm;
    }

}
