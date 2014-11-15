/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.license.plugin.action.get;

import org.elasticsearch.action.admin.cluster.ClusterAction;
import org.elasticsearch.client.ClusterAdminClient;

public class GetLicenseAction extends ClusterAction<GetLicenseRequest, GetLicenseResponse, GetLicenseRequestBuilder> {

    public static final GetLicenseAction INSTANCE = new GetLicenseAction();
    public static final String NAME = "cluster:admin/plugin/license/get";

    private GetLicenseAction() {
        super(NAME);
    }

    @Override
    public GetLicenseResponse newResponse() {
        return new GetLicenseResponse();
    }

    @Override
    public GetLicenseRequestBuilder newRequestBuilder(ClusterAdminClient client) {
        return new GetLicenseRequestBuilder(client);
    }
}