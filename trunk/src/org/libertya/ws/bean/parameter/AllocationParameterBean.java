package org.libertya.ws.bean.parameter;

import java.util.ArrayList;
import java.util.HashMap;

public class AllocationParameterBean extends ParameterBean {

	/* Constantes utilizadas para especificar el tipo de anulacion de recibos de cliente */
	/** Revertir recibo */
	public static final String ALLOCATIONACTION_RevertAllocation = "RX";
	/** Revertir y Anular Cobros */
	public static final String ALLOCATIONACTION_VoidPayments = "VP";
	/** Revertir y Anular Cobros y Retenciones */
	public static final String ALLOCATIONACTION_VoidPaymentsRetentions = "VR";
	
	/** Facturas a cancelar con los medios de pago correspondientes */
	protected ArrayList<HashMap<String, String>> invoices = new ArrayList<HashMap<String, String>>();
	
	/** Medios de pago a utilizar en la cancelacion de las facturas, los cuales pueden ser:
	 *  	Efectivo, Transferencia Bancaria, Cheque, Tarjeta de Credito, 
	 *  	Nota de Credito, Retencion, Pago anticipado.
	 *
	 *  El medio de pago se encuentra determinado por el C_POSPaymentMedium_ID especificado
	 */
	protected ArrayList<HashMap<String, String>> payments = new ArrayList<HashMap<String, String>>();
	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public AllocationParameterBean() {
		super();
	}
	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public AllocationParameterBean(String userName, String password, int clientID,	int orgID) {
		super(userName, password, clientID, orgID);
	}

	
	
	/**
	 * Adiciona una nueva factura a cancelar, correspondiente a un conjunto de pares columna / valor
	 */
	public void newInvoice() {
		invoices.add(new HashMap<String, String>());
	}

	/**
	 * Incorpora una nueva columna a los datos de la factura actual 
	 * @param columnName nombre de la columna
	 * @param columnValue valor de la columna
	 */
	public void addColumnToCurrentInvoice(String columnName, String columnValue) {
		addColumnOnTable(invoices.get(invoices.size()-1), columnName, columnValue);
	}
	

	/**
	 * Adiciona un nuevo pago para cancelar las facturas
	 */
	public void newPayment() {
		payments.add(new HashMap<String, String>());
	}

	/**
	 * Incorpora una nueva columna a los datos del pago actual  
	 * @param columnName nombre de la columna
	 * @param columnValue valor de la columna
	 */
	public void addColumnToCurrentPayment(String columnName, String columnValue) {
		addColumnOnTable(payments.get(payments.size()-1), columnName, columnValue);
	}
	
	/**
	 * Incorpora una nueva columna a la cabecera del recibo  
	 * @param columnName nombre de la columna
	 * @param columnValue valor de la columna
	 */
	public void addColumnToHeader(String columnName, String columnValue) {
		addColumnToMainTable(columnName, columnValue);
	}


	/**
	 * Getter tradicional 
	 * @return la nómina de facturas a cancelar
	 */
	public ArrayList<HashMap<String, String>> getInvoices() {
		return invoices;
	}

	
	/**
	 * Getter tradicional
	 * @return la nómina de pagos para cancelar las facturas
	 */
	public ArrayList<HashMap<String, String>> getPayments() {
		return payments;
	}

	/**
	 * Setter tradicional
	 * @param invoices nomina de facturas a cobrar
	 */
	public void setInvoices(ArrayList<HashMap<String, String>> invoices) {
		this.invoices = invoices;
	}

	/**
	 * Setter tradicional nomina de pagos para cancelar las facturas
	 * @param payments
	 */
	public void setPayments(ArrayList<HashMap<String, String>> payments) {
		this.payments = payments;
	}	


	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n  Invoices: ");
		if (invoices!=null)
			for (HashMap<String, String> anInvoice : invoices)
				if (anInvoice!=null) {
					out.append("\n    ");
					for (String key : anInvoice.keySet())
						out.append(key).append(" = ").
							append(anInvoice.get(key)).
							append("; ");
				}
		out.append("\n  Payments: ");
		if (payments!=null)
			for (HashMap<String, String> aPayment : payments)
				if (aPayment!=null) {
					out.append("\n    ");
					for (String key : aPayment.keySet())
						out.append(key).append(" = ").
							append(aPayment.get(key)).
							append("; ");
				}
		return out.toString();
	}
	
}
