/*
 * VarInfo.java
 *
 * Created on November 12, 2006, 9:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.subsetting;
//import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class VarInfo {
	private Boolean checked;
	private String varType;
	private Long varId;
	private String varName;
	private String varLabel;

	/**
	 * Creates a new instance of VarInfo
	 */
	public VarInfo() {
	
	}
	
	public VarInfo(
		Boolean checked,
		String varType, 
		Long varId, 
		String varName, 
		String varLabel
	){
		setChecked(checked);
		setVarType(varType);
		setVarId(varId);
		setVarName(varName);
		setVarLabel(varLabel);
	}

	public String toString(){
		String chkboxstate;
		if (this.getChecked()) {
			chkboxstate="selected";
		} else {
			chkboxstate="notSelected";
		}
		StringBuffer buf = new StringBuffer();
		buf.append(chkboxstate).append(",");
		buf.append(this.varId).append(", "); 
		buf.append(this.varType).append(", ");
		buf.append(this.varLabel).append(",");
		return buf.toString();
	}
	
	public String getVarType() {
		return varType;
	}

	public void setVarType (String varType){
		this.varType = varType;
	}

	public Long getVarId() {
		return varId;
	}

	public void setVarId (Long varId){
		this.varId = varId;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName (String varName){
		this.varName = varName;
	}

	public String getVarLabel() {
		return varLabel;
	}

	public void setVarLabel (String varLabel){
		this.varLabel = varLabel;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
}
