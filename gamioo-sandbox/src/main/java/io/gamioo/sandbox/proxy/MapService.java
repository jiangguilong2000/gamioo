package io.gamioo.sandbox.proxy;

/**
 * @author Allen Jiang
 */
public class MapService extends AbstractService {

    private MapServiceProxy mapServiceProxy;

    private static MapService instance;

    public static MapServiceProxy get(int id) {
        if(instance==null){
            instance=new MapService();
        }
       return  instance.getProxy(id);

    }


    public MapServiceProxy getProxy(Object id) {
        if (mapServiceProxy == null) {
            MapServiceProxy mapServiceProxy = new MapServiceProxyImpl();
            this.mapServiceProxy = (MapServiceProxy) this.getProxy(id, mapServiceProxy);
        }
        return this.mapServiceProxy;
    }
}