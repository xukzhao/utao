package tv.utao.x5.domain;

public class SysInfo {
    private String versionName;
    private Boolean is64;

    private Integer versionCode;
    private Boolean haveNew;
    private Boolean x5Ok;
    private String cacheSize;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Boolean getIs64() {
        return is64;
    }

    public void setIs64(Boolean is64) {
        this.is64 = is64;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public Boolean getHaveNew() {
        return haveNew;
    }

    public void setHaveNew(Boolean haveNew) {
        this.haveNew = haveNew;
    }

    public Boolean getX5Ok() {
        return x5Ok;
    }

    public void setX5Ok(Boolean x5Ok) {
        this.x5Ok = x5Ok;
    }

    public String getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }
}
