/**
 * Copyright 2015 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.client;

import static org.fcrepo.client.FedoraHeaderConstants.CONTENT_DISPOSITION;
import static org.fcrepo.client.FedoraHeaderConstants.SLUG;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Builds a post request for interacting with the Fedora HTTP API in order to create a new resource within an LDP
 * container.
 * 
 * @author bbpennel
 */
public class PostBuilder<T extends PostBuilder<T>> extends BodyRequestBuilder<PostBuilder<T>> {

    protected String filename;

    protected String slug;

    /**
     * Instantiate builder
     * 
     * @param uri uri of the resource this request is being made to
     * @param client the client
     */
    public PostBuilder(final URI uri, final FcrepoClient client) {
        super(uri, client);
    }

    @Override
    protected PostBuilder<T> self() {
        return this;
    }

    /**
     * Provide a SHA-1 checksum for the body of this request
     * 
     * @param digest sha-1 checksum to provide as the digest for the request body
     * @return this builder
     */
    public PostBuilder<T> digest(final String digest) {
        this.digest = digest;
        return self();
    }

    @Override
    protected void populateRequest(final HttpRequestBase request) throws FcrepoOperationFailedException {
        if (slug != null) {
            request.addHeader(SLUG, slug);
        }

        if (filename != null) {
            try {
                final String encodedFilename = URLEncoder.encode(filename, "utf-8");
                final String disposition = "attachment; filename=\"" + encodedFilename + "\"";
                request.addHeader(CONTENT_DISPOSITION, disposition);
            } catch (UnsupportedEncodingException e) {
                throw new FcrepoOperationFailedException(request.getURI(), -1, e.getMessage());
            }

        }

        super.populateRequest(request);
    }

    @Override
    protected HttpRequestBase createRequest() {
        final HttpMethods method = HttpMethods.POST;
        return (HttpEntityEnclosingRequestBase) method.createRequest(targetUri);
    }

    /**
     * Provide a content disposition header which will be used as the filename
     * 
     * @param filename the name of the file being provided in the body of the request
     * @return this builder
     */
    public PostBuilder<T> filename(final String filename) {
        this.filename = filename;
        return self();
    }

    /**
     * Provide a suggested name for the new child resource, which the repository may ignore.
     * 
     * @param slug value to supply as the slug header
     * @return this builder
     */
    public PostBuilder<T> slug(final String slug) {
        this.slug = slug;
        return self();
    }
}
