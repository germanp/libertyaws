package org.libertya.ws.bean.parameter;

public class OrderParameterBean extends DocumentParameterBean {

	
	/* Constantes utilizadas para especificar el tipo de comprobante de la factura */
	/** Tipo de documento a generar: Factura */
	public static final String TIPO_COMPROBANTE_FACTURA 		= "FC";
	/** Tipo de documento a generar: Nota de débito */
	public static final String TIPO_COMPROBANTE_NOTA_DE_DEBITO 	= "ND";
	/** Tipo de documento a generar: Nota de crédito */
	public static final String TIPO_COMPROBANTE_NOTA_DE_CREDITO	= "NC";
	
	/** Tipo de documento de la factura a crear a partir del pedido (en caso de crearla) */
	protected int invoiceDocTypeTargetID 	= 0;
	/** Nro de punto de venta de la factura a crear a partir del pedido (en caso de crearla) */
	protected int invoicePuntoDeVenta 		= 0;
	/** Tipo de comprobante de la factura a crear a partir del pedido (en caso de crearla).  Ver constantes asociadas */
	protected String invoiceTipoComprobante = null;
	
	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public OrderParameterBean() {
		super();
	}

	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public OrderParameterBean(String userName, String password, int clientID,	int orgID) {
		super(userName, password, clientID, orgID);
	}
	
	
	/** Basic getter para el tipo de documento de la factura */
	public int getInvoiceDocTypeTargetID() {
		return invoiceDocTypeTargetID;
	}
	
	/** Basic setter para el tipo de documento de la factura */
	public void setInvoiceDocTypeTargetID(int invoiceDocTypeTargetID) {
		this.invoiceDocTypeTargetID = invoiceDocTypeTargetID;
	}
	
	/** Basic getter para el punto de venta de la factura */
	public int getInvoicePuntoDeVenta() {
		return invoicePuntoDeVenta;
	}
	
	/** Basic setter para el punto de venta de la factura */
	public void setInvoicePuntoDeVenta(int invoicePuntoDeVenta) {
		this.invoicePuntoDeVenta = invoicePuntoDeVenta;
	}
	
	/** Basic getter para el tipo de comprobante */
	public String getInvoiceTipoComprobante() {
		return invoiceTipoComprobante;
	}
	
	/** Basic setter para el tipo de comprobante */
	public void setInvoiceTipoComprobante(String invoiceTipoComprobante) {
		this.invoiceTipoComprobante = invoiceTipoComprobante;
	}
	
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n  ");
		out.append("invoiceDocTypeTargetID = ").append(invoiceDocTypeTargetID).append("; ");
		out.append("invoicePuntoDeVenta = ").append(invoicePuntoDeVenta).append("; ");
		out.append("invoiceTipoComprobante = ").append(invoiceTipoComprobante);
		return out.toString();
	}
}
