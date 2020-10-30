package com.finastra.ffdc.dataset.component.dtos;

import lombok.Data;

@Data
public class SasResponse {
    public String protocol;
    public String dataSetId;
    public String fileName;
    public String encryptionJobId;
    public String status;
    public String singleUseToken;
    public String blobUrl;
    public String jobId;
    public String created;
    public String createdBy;
    public String lastModified;
    public String modifiedBy;
}
