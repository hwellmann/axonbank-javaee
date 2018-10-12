/*
 * Copyright 2018 OPS4J Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.axon.bank.rest;

import java.util.Collections;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;

/**
 * @author Harald Wellmann
 *
 */
@Provider
@Priority(Interceptor.Priority.APPLICATION)
public class AllowAllCorsFilter extends CorsFilter {


    public AllowAllCorsFilter() {
        allowedOrigins = Collections.singleton("*");
        allowedMethods = "OPTIONS, GET, POST, DELETE, PUT, PATCH";
    }
}
