package org.libertya.ws.utils;

import java.util.Properties;

import org.openXpertya.util.Env;

@SuppressWarnings("serial")
public final class LYWSServerContext extends Properties
{
    private LYWSServerContext()
    {
        super();
        /**
         * Set es_AR as default language
         */
        this.put(Env.LANGUAGE, "es_AR"); 	// FIXME: Desharcode, o ampliar ParameterBean y poner valor por defecto        
    }
    
    private static InheritableThreadLocal<LYWSServerContext> context = new InheritableThreadLocal<LYWSServerContext>() {
        protected LYWSServerContext initialValue()
        {
            return new LYWSServerContext();
        }
    };
    
    /**
     * Get server context for current thread
     * @return ServerContext
     */
    public static LYWSServerContext getCurrentInstance()
    {
        return (LYWSServerContext)context.get();
    }
    
    /**
     * dispose server context for current thread
     */
    public static void dispose()
    {
    	context.remove();
    }
    
    /**
     * Allocate new server context for current thread
     * @return ServerContext
     */
    public static LYWSServerContext newInstance() 
    {
    	dispose();
    	return getCurrentInstance();
    }
    
    /**
     * Set server context for current thread
     * @param ctx
     */
    public static void setCurrentInstance(LYWSServerContext ctx)
    {
        context.set(ctx);
    }
}
