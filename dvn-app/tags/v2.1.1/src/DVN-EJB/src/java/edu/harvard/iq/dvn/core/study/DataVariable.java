/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * DataVariable.java
 *
 * Created on August 9, 2006, 3:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.GenerationType;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Version;


/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class DataVariable implements java.io.Serializable{
    
    /** Creates a new instance of DataVariable */
    public DataVariable() {
    }

    /**
     * Holds value of property studyFile.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
    private DataTable dataTable;

    /**
     * Getter for property studyFile.
     * @return Value of property studyFile.
     */
    public DataTable getDataTable() {
        return this.dataTable;
    }

    /**
     * Setter for property studyFile.
     * @param studyFile New value of property studyFile.
     */
    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    /**
     * Holds value of property id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Holds value of property name.
     */
    private String name;

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Holds value of property label.
     */
    @Column(columnDefinition="TEXT")
    private String label;

    /**
     * Getter for property label.
     * @return Value of property label.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Setter for property label.
     * @param label New value of property label.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Holds value of property weighted.
     */
    private boolean weighted;

    /**
     * Getter for property weight.
     * @return Value of property weight.
     */
    public boolean isWeighted() {
        return this.weighted;
    }

    /**
     * Setter for property weight.
     * @param weight New value of property weight.
     */
    public void setWeighted(boolean weighted) {
        this.weighted = weighted;
    }

    /**
     * Holds value of property fileStartPosition.
     */
    private java.lang.Long fileStartPosition;

    /**
     * Getter for property fileStartPosition.
     * @return Value of property fileStartPosition.
     */
    public java.lang.Long getFileStartPosition() {
        return this.fileStartPosition;
    }

    /**
     * Setter for property fileStartPosition.
     * @param fileStartPosition New value of property fileStartPosition.
     */
    public void setFileStartPosition(java.lang.Long fileStartPosition) {
        this.fileStartPosition = fileStartPosition;
    }

    /**
     * Holds value of property fileEndPosition.
     */
    private java.lang.Long fileEndPosition;

    /**
     * Getter for property fileEndPosition.
     * @return Value of property fileEndPosition.
     */
    public java.lang.Long getFileEndPosition() {
        return this.fileEndPosition;
    }

    /**
     * Setter for property fileEndPosition.
     * @param fileEndPosition New value of property fileEndPosition.
     */
    public void setFileEndPosition(java.lang.Long fileEndPosition) {
        this.fileEndPosition = fileEndPosition;
    }

    /**
     * Holds value of property formatSchema.
     */
    private String formatSchema;

    /**
     * Getter for property formatSchema.
     * @return Value of property formatSchema.
     */
    public String getFormatSchema() {
        return this.formatSchema;
    }

    /**
     * Setter for property formatSchema.
     * @param formatSchema New value of property formatSchema.
     */
    public void setFormatSchema(String formatSchema) {
        this.formatSchema = formatSchema;
    }

    /**
     * Holds value of property formatSchemaName.
     */
    private String formatSchemaName;

    /**
     * Getter for property formatSchemaName.
     * @return Value of property formatSchemaName.
     */
    public String getFormatSchemaName() {
        return this.formatSchemaName;
    }

    /**
     * Setter for property formatSchemaName.
     * @param formatSchemaName New value of property formatSchemaName.
     */
    public void setFormatSchemaName(String formatSchemaName) {
        this.formatSchemaName = formatSchemaName;
    }

    /**
     * Holds value of property variableIntervalType.
     */
    @ManyToOne
    private VariableIntervalType variableIntervalType;

    /**
     * Getter for property variableIntervalType.
     * @return Value of property variableIntervalType.
     */
    public VariableIntervalType getVariableIntervalType() {
        return this.variableIntervalType;
    }

    /**
     * Setter for property variableIntervalType.
     * @param variableIntervalType New value of property variableIntervalType.
     */
    public void setVariableIntervalType(VariableIntervalType variableIntervalType) {
        this.variableIntervalType = variableIntervalType;
    }

    /**
     * Holds value of property variableFormatType.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
    private VariableFormatType variableFormatType;

    /**
     * Getter for property variableFormatType.
     * @return Value of property variableFormatType.
     */
    public VariableFormatType getVariableFormatType() {
        return this.variableFormatType;
    }

    /**
     * Setter for property variableFormatType.
     * @param variableFormatType New value of property variableFormatType.
     */
    public void setVariableFormatType(VariableFormatType variableFormatType) {
        this.variableFormatType = variableFormatType;
    }

    /**
     * Holds value of property recordSegmentNumber.
     */
    private java.lang.Long recordSegmentNumber;

    /**
     * Getter for property recordSegmentNumber.
     * @return Value of property recordSegmentNumber.
     */
    public java.lang.Long getRecordSegmentNumber() {
        return this.recordSegmentNumber;
    }

    /**
     * Setter for property recordSegmentNumber.
     * @param recordSegmentNumber New value of property recordSegmentNumber.
     */
    public void setRecordSegmentNumber(java.lang.Long recordSegmentNumber) {
        this.recordSegmentNumber = recordSegmentNumber;
    }

    /**
     * Holds value of property invalidRanges.
     */
    @OneToMany (mappedBy="dataVariable", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<VariableRange> invalidRanges;

    /**
     * Getter for property invalidRanges.
     * @return Value of property invalidRanges.
     */
    public java.util.Collection<edu.harvard.iq.dvn.core.study.VariableRange> getInvalidRanges() {
        return this.invalidRanges;
    }

    /**
     * Setter for property invalidRanges.
     * @param invalidRanges New value of property invalidRanges.
     */
    public void setInvalidRanges(java.util.Collection<edu.harvard.iq.dvn.core.study.VariableRange> invalidRanges) {
        this.invalidRanges = invalidRanges;
    }

    /**
     * Holds value of property invalidRangeItems.
     */
     @OneToMany (mappedBy="dataVariable", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private java.util.Collection<VariableRangeItem> invalidRangeItems;

    /**
     * Getter for property invalidRangeItems.
     * @return Value of property invalidRangeItems.
     */
    public java.util.Collection<VariableRangeItem> getInvalidRangeItems() {
        return this.invalidRangeItems;
    }

    /**
     * Setter for property invalidRangeItems.
     * @param invalidRangeItems New value of property invalidRangeItems.
     */
    public void setInvalidRangeItems(java.util.Collection<VariableRangeItem> invalidRangeItems) {
        this.invalidRangeItems = invalidRangeItems;
    }

    /**
     * Holds value of property summaryStatistics.
     */
    @OneToMany (mappedBy="dataVariable", cascade={ CascadeType.REMOVE, CascadeType.MERGE,CascadeType.PERSIST})
    private Collection<SummaryStatistic> summaryStatistics;

    /**
     * Getter for property summaryStatistics.
     * @return Value of property summaryStatistics.
     */
    public Collection<SummaryStatistic> getSummaryStatistics() {
        return this.summaryStatistics;
    }

    /**
     * Setter for property summaryStatistics.
     * @param summaryStatistics New value of property summaryStatistics.
     */
    public void setSummaryStatistics(Collection<SummaryStatistic> summaryStatistics) {
        this.summaryStatistics = summaryStatistics;
    }

    /**
     * Holds value of property unf.
     */
    private String unf;

    /**
     * Getter for property unf.
     * @return Value of property unf.
     */
    public String getUnf() {
        return this.unf;
    }

    /**
     * Setter for property unf.
     * @param unf New value of property unf.
     */
    public void setUnf(String unf) {
        this.unf = unf;
    }

    /**
     * Holds value of property categories.
     */
    @OneToMany (mappedBy="dataVariable", cascade={ CascadeType.REMOVE, CascadeType.MERGE,CascadeType.PERSIST})
    private Collection<VariableCategory> categories;

    /**
     * Getter for property categories.
     * @return Value of property categories.
     */
    public Collection<VariableCategory> getCategories() {
        return this.categories;
    }

    /**
     * Setter for property categories.
     * @param categories New value of property categories.
     */
    public void setCategories(Collection<VariableCategory> categories) {
        this.categories = categories;
    }

    /**
     * Holds value of property weightedVariables.
     */
    @OneToMany (mappedBy="dataVariable")
    private java.util.Collection<WeightedVarRelationship> weightedVariables;

    /**
     * Getter for property weightedVariables.
     * @return Value of property weightedVariables.
     */
    public java.util.Collection<WeightedVarRelationship> getWeightedVariables() {
        return this.weightedVariables;
    }

    /**
     * Setter for property weightedVariables.
     * @param weightedVariables New value of property weightedVariables.
     */
    public void setWeightedVariables(java.util.Collection<edu.harvard.iq.dvn.core.study.WeightedVarRelationship> weightedVariables) {
        this.weightedVariables = weightedVariables;
    }

    /**
     * Holds value of property concept.
     */
    private String concept;

    /**
     * Getter for property concept.
     * @return Value of property concept.
     */
    public String getConcept() {
        return this.concept;
    }

    /**
     * Setter for property concept.
     * @param concept New value of property concept.
     */
    public void setConcept(String concept) {
        this.concept = concept;
    }

    /**
     * Holds value of property universe.
     */
    private String universe;

    /**
     * Getter for property universe.
     * @return Value of property universe.
     */
    public String getUniverse() {
        return this.universe;
    }

    /**
     * Setter for property universe.
     * @param universe New value of property universe.
     */
    public void setUniverse(String universe) {
        this.universe = universe;
    }

    /**
     * Holds value of property questionText.
     */
    @Column(columnDefinition="TEXT")
    private String questionText;

    /**
     * Getter for property questionText.
     * @return Value of property questionText.
     */
    public String getQuestionText() {
        return this.questionText;
    }

    /**
     * Setter for property questionText.
     * @param questionText New value of property questionText.
     */
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    
    private int fileOrder;

    public int getFileOrder() {
        return fileOrder;
    }

    public void setFileOrder(int fileOrder) {
        this.fileOrder = fileOrder;
    }
    
   public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataVariable)) {
            return false;
        }
        DataVariable other = (DataVariable)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
    
    
     /**
     * Holds value of property formatSchema.
     */
    private String formatCategory;

    public String getFormatCategory() {
        return formatCategory;
    }

    public void setFormatCategory(String formatCategory) {
        this.formatCategory = formatCategory;
    }
    
    private Long numberOfDecimalPoints;

    public Long getNumberOfDecimalPoints() {
        return numberOfDecimalPoints;
    }

    public void setNumberOfDecimalPoints(Long numberOfDecimalPoints) {
        this.numberOfDecimalPoints = numberOfDecimalPoints;
    }
    
    

    
    /**
     * Holds value of property version.
     */
    @Version
    private Long version;

    /**
     * Getter for property version.
     * @return Value of property version.
     */
    public Long getVersion() {
        return this.version;
    }

    /**
     * Setter for property version.
     * @param version New value of property version.
     */
    public void setVersion(Long version) {
        this.version = version;
    }        

//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this,
//            ToStringStyle.MULTI_LINE_STYLE);
//    }

}
