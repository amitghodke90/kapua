/*******************************************************************************
 * Copyright (c) 2011, 2017 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.device.registry.connection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.swagger.annotations.ApiModelProperty;
import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

/**
 * Device connection entity definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "deviceConnection")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "status",
        "clientId",
        "userId",
        "protocol",
        "clientIp",
        "serverIp" }, //
        factoryClass = DeviceConnectionXmlRegistry.class, //
        factoryMethod = "newDeviceConnection")
public interface DeviceConnection extends KapuaUpdatableEntity {

    public static final String TYPE = "deviceConnection";

    public default String getType() {
        return TYPE;
    }

    /**
     * Get the device connection status
     *
     * @return
     */
    @XmlElement(name = "status")
    public DeviceConnectionStatus getStatus();

    /**
     * Set the device connection status
     *
     * @param status
     */
    public void setStatus(DeviceConnectionStatus status);

    /**
     * Get the client identifier
     *
     * @return
     */
    @XmlElement(name = "clientId")
    public String getClientId();

    /**
     * Set the client identifier
     *
     * @param clientId
     */
    public void setClientId(String clientId);

    /**
     * Get the user identifier
     *
     * @return
     */
    @XmlElement(name = "userId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @ApiModelProperty(dataType = "string")
    public KapuaId getUserId();

    /**
     * Set the user identifier
     *
     * @param userId
     */
    public void setUserId(KapuaId userId);

    /**
     * Get the device protocol
     *
     * @return
     */
    @XmlElement(name = "protocol")
    public String getProtocol();

    /**
     * Set the device protocol
     *
     * @param protocol
     */
    public void setProtocol(String protocol);

    /**
     * Get the client ip
     *
     * @return
     */
    @XmlElement(name = "clientIp")
    public String getClientIp();

    /**
     * Set the client ip
     *
     * @param clientIp
     */
    public void setClientIp(String clientIp);

    /**
     * Get the server ip
     *
     * @return
     */
    @XmlElement(name = "serverIp")
    public String getServerIp();

    /**
     * Set the server ip
     *
     * @param serverIp
     */
    public void setServerIp(String serverIp);
}
