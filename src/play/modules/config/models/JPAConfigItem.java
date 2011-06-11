package play.modules.config.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import play.Logger;
import play.db.jpa.GenericModel;
import play.db.jpa.JPQL;
import play.modules.config.ConfigPlugin;

/**
 * Default {@link IConfigItem} implementation on JPA
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0 18/12/2010
 */
@Entity
@Table(name = "play_conf")
public class JPAConfigItem extends GenericModel implements IConfigItem {

    /**
     * 
     */
    private static final long serialVersionUID = -8415264008115090076L;

    @Id
    public String _key;

    public String value;
    
    public String app_id;
    
    @PrePersist
    public void setAppId() {
        app_id = ConfigPlugin.appId();
    }

    public JPAConfigItem() {
    }

    private JPAConfigItem(String key, String value) {
        this._key = key;
        this.value = value;
    }

    @Override
    public String pc_key() {
        return _key;
    }

    @Override
    public String pc_value() {
        return value;
    }

    @Override
    public IConfigItem pc_value(String value) {
        this.value = value;
        return this;
    }

    public IConfigItem pc_save() {
        return (IConfigItem) save();
    }

    @Override
    public String toString() {
        return String.format("%1$s=%2$s", _key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IConfigItem> pc_all() {
        List<IConfigItem> l = new ArrayList<IConfigItem>();
        List<JPAConfigItem> l0 = JPQL.instance.findBy(
                JPAConfigItem.class.getName(), "app_id = ? order by _key", new Object[] {app_id});
        for (JPAConfigItem ci : l0) {
            l.add(ci);
        }
        return l;
    }

    @Override
    public IConfigItem pc_new(String key, String value) {
        ConfigPlugin.assertValidKey(key);
        return new JPAConfigItem(key, value);
    }

    @Override
    public IConfigItem pc_findByKey(String key) {
        try {
            return (IConfigItem) JPQL.instance.findById(
                    JPAConfigItem.class.getName(), key);
        } catch (Exception e) {
            Logger.warn(e, "Error fetch configuration item by key: %1$s", key);
            return null;
        }
    }

    @Override
    public void pc_clear() {
        JPQL.instance.deleteAll(JPAConfigItem.class.getName());
    }

    @Override
    public void pc_delete() {
        delete();
    }
    
    @Override
    public void onApplicationStart() {
        //TODO add support to multidb
    }

}
