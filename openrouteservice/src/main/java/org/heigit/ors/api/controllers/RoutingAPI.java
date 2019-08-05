/*
 * This file is part of Openrouteservice.
 *
 * Openrouteservice is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, see <https://www.gnu.org/licenses/>.
 */

package org.heigit.ors.api.controllers;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.vividsolutions.jts.geom.Coordinate;
import org.heigit.ors.api.errors.CommonResponseEntityExceptionHandler;
import org.heigit.ors.api.requests.common.APIEnums;
import org.heigit.ors.api.requests.routing.RouteRequest;
import org.heigit.ors.api.requests.routing.RouteRequestHandler;
import org.heigit.ors.api.responses.routing.GPXRouteResponseObjects.GPXRouteResponse;
import org.heigit.ors.api.responses.routing.GeoJSONRouteResponseObjects.GeoJSONRouteResponse;
import org.heigit.ors.api.responses.routing.JSONRouteResponseObjects.JSONRouteResponse;
import org.heigit.ors.common.StatusCode;
import org.heigit.ors.exceptions.*;
import org.heigit.ors.routing.RouteResult;
import org.heigit.ors.routing.RoutingErrorCodes;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@Api(value = "Directions Service", description = "Get directions for different modes of transport", tags = "Directions")
@RequestMapping("/v2/directions")
@ApiResponses({
        @ApiResponse(code = 400, message = "The request is incorrect and therefore can not be processed."),
        @ApiResponse(code = 404, message = "An element could not be found. If possible, a more detailed error code is provided."),
        @ApiResponse(code = 405, message = "The specified HTTP method is not supported. For more details, refer to the EndPoint documentation."),
        @ApiResponse(code = 413, message = "The request is larger than the server is able to process, the data provided in the request exceeds the capacity limit."),
        @ApiResponse(code = 500, message = "An unexpected error was encountered and a more detailed error code is provided."),
        @ApiResponse(code = 501, message = "Indicates that the server does not support the functionality needed to fulfill the request."),
        @ApiResponse(code = 503, message = "The server is currently unavailable due to overload or maintenance.")
})
public class RoutingAPI {
    static final CommonResponseEntityExceptionHandler errorHandler = new CommonResponseEntityExceptionHandler(RoutingErrorCodes.BASE);

    // generic catch methods - when extra info is provided in the url, the other methods are accessed.
    @GetMapping
    @ApiOperation(value = "", hidden = true)
    public void getGetMapping() throws MissingParameterException {
        throw new MissingParameterException(RoutingErrorCodes.MISSING_PARAMETER, "profile");
    }

    @PostMapping
    @ApiOperation(value = "", hidden = true)
    public String getPostMapping(@RequestBody RouteRequest request) throws MissingParameterException {
        throw new MissingParameterException(RoutingErrorCodes.MISSING_PARAMETER, "profile");
    }

    // Matches any response type that has not been defined
    @PostMapping(value="/{profile}/*")
    @ApiOperation(value = "", hidden = true)
    public void getInvalidResponseType() throws StatusCodeException {
        throw new StatusCodeException(HttpServletResponse.SC_NOT_ACCEPTABLE, RoutingErrorCodes.UNSUPPORTED_EXPORT_FORMAT, "This response format is not supported");
    }

    // Functional request methods

    @GetMapping(value = "/{profile}", produces = {"application/geo+json;charset=UTF-8"})
    @ApiOperation(notes = "Get a basic route between two points with the profile provided. Returned response is in GeoJSON format. " +
            "This method does not accept any request body or parameters other than profile, start coordinate, and end coordinate.", value = "Directions Service (GET)", httpMethod = "GET")
    @ApiResponses(
            @ApiResponse(code = 200,
                    message = "Standard response for successfully processed requests. Returns GeoJSON. The decoded values of the extra information can be found [here](https://github.com/GIScience/openrouteservice-docs).",
                    response = GeoJSONRouteResponse.class)
    )
    public GeoJSONRouteResponse getSimpleGeoJsonRoute(@ApiParam(value = "Specifies the route profile.", required = true, example = "driving-car") @PathVariable APIEnums.Profile profile,
                                                      @ApiParam(value = "Start coordinate of the route", required = true, example = "8.681495,49.41461") @RequestParam Coordinate start,
                                                      @ApiParam(value = "Destination coordinate of the route", required = true, example = "8.687872,49.420318") @RequestParam Coordinate end) throws StatusCodeException{
        RouteRequest request = new RouteRequest(start, end);
        request.setProfile(profile);

        RouteResult[] result = new RouteRequestHandler().generateRouteFromRequest(request);

        return new GeoJSONRouteResponse(result, request);
    }

    @PostMapping(value = "/{profile}")
    @ApiOperation(notes = "Returns a route between two or more locations for a selected profile and its settings as JSON", value = "Directions Service (POST)", httpMethod = "POST", consumes = "application/json", produces = "application/json")
    @ApiResponses(
            @ApiResponse(code = 200,
                    message = "Standard response for successfully processed requests. Returns JSON. The decoded values of the extra information can be found [here](https://github.com/GIScience/openrouteservice-docs).",
                    response = JSONRouteResponse.class)
    )
    public JSONRouteResponse getDefault(@ApiParam(value = "Specifies the route profile.", required = true, example = "driving-car") @PathVariable APIEnums.Profile profile,
                                        @ApiParam(value = "The request payload", required = true) @RequestBody RouteRequest request) throws StatusCodeException {
        return getJsonRoute(profile, request);
    }

    @PostMapping(value = "/{profile}/json", produces = {"application/json;charset=UTF-8"})
    @ApiOperation(notes = "Returns a route between two or more locations for a selected profile and its settings as JSON", value = "Directions Service JSON (POST)", httpMethod = "POST", consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JSON Response", response = JSONRouteResponse.class)
    })
    public JSONRouteResponse getJsonRoute(
            @ApiParam(value = "Specifies the route profile.", required = true, example = "driving-car") @PathVariable APIEnums.Profile profile,
            @ApiParam(value = "The request payload", required = true) @RequestBody RouteRequest request) throws StatusCodeException {
        request.setProfile(profile);
        request.setResponseType(APIEnums.RouteResponseType.JSON);

        RouteResult[] result = new RouteRequestHandler().generateRouteFromRequest(request);

        return new JSONRouteResponse(result, request);
    }

    @PostMapping(value = "/{profile}/gpx", produces = "application/gpx+xml;charset=UTF-8")
    @ApiOperation(notes = "Returns a route between two or more locations for a selected profile and its settings as GPX. The schema can be found [here](https://raw.githubusercontent.com/GIScience/openrouteservice-schema/master/gpx/v1/ors-gpx.xsd)",
            value = "Directions Service GPX (POST)", httpMethod = "POST", consumes = "application/json", produces = "application/gpx+xml")
    @ApiResponses(value = {
            @ApiResponse(code = 200,
                    message = "Standard response for successfully processed requests. Returns GPX.",
                    response = GPXRouteResponse.class)
    })
    public GPXRouteResponse getGPXRoute(
            @ApiParam(value = "Specifies the route profile.", required = true, example = "driving-car") @PathVariable APIEnums.Profile profile,
            @ApiParam(value = "The request payload", required = true) @RequestBody RouteRequest request) throws StatusCodeException {
        request.setProfile(profile);
        request.setResponseType(APIEnums.RouteResponseType.GPX);

        RouteResult[] result = new RouteRequestHandler().generateRouteFromRequest(request);

        return new GPXRouteResponse(result, request);

    }

    @PostMapping(value = "/{profile}/geojson", produces = "application/geo+json;charset=UTF-8")
    @ApiOperation(notes = "Returns a route between two or more locations for a selected profile and its settings as GeoJSON", value = "Directions Service GeoJSON (POST)", httpMethod = "POST", consumes = "application/json", produces = "application/geo+json")
    @ApiResponses(value = {
            @ApiResponse(code = 200,
                    message = "Standard response for successfully processed requests. Returns GeoJSON. The decoded values of the extra information can be found [here](https://github.com/GIScience/openrouteservice-docs).",
                    response = GeoJSONRouteResponse.class)
    })
    public GeoJSONRouteResponse getGeoJsonRoute(
            @ApiParam(value = "Specifies the route profile.", required = true, example = "driving-car") @PathVariable APIEnums.Profile profile,
            @ApiParam(value = "The request payload", required = true) @RequestBody RouteRequest request) throws StatusCodeException {
        request.setProfile(profile);
        request.setResponseType(APIEnums.RouteResponseType.GEOJSON);

        RouteResult[] result = new RouteRequestHandler().generateRouteFromRequest(request);

        return new GeoJSONRouteResponse(result, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingParams(final MissingServletRequestParameterException e) {
        return errorHandler.handleStatusCodeException(new MissingParameterException(RoutingErrorCodes.MISSING_PARAMETER, e.getParameterName()));
    }


    @ExceptionHandler({HttpMessageNotReadableException.class, HttpMessageConversionException.class, Exception.class})
    public ResponseEntity<Object> handleReadingBodyException(final Exception e) {
        final Throwable cause = e.getCause();
        if (cause instanceof UnrecognizedPropertyException) {
            return errorHandler.handleUnknownParameterException(new UnknownParameterException(RoutingErrorCodes.UNKNOWN_PARAMETER, ((UnrecognizedPropertyException) cause).getPropertyName()));
        } else if (cause instanceof InvalidFormatException) {
            return errorHandler.handleStatusCodeException(new ParameterValueException(RoutingErrorCodes.INVALID_PARAMETER_FORMAT, ((InvalidFormatException) cause).getValue().toString()));
        } else if (cause instanceof InvalidDefinitionException) {
            return errorHandler.handleStatusCodeException(new ParameterValueException(RoutingErrorCodes.INVALID_PARAMETER_VALUE, ((InvalidDefinitionException) cause).getPath().get(0).getFieldName()));
        } else if (cause instanceof MismatchedInputException) {
            return errorHandler.handleStatusCodeException(new ParameterValueException(RoutingErrorCodes.INVALID_PARAMETER_FORMAT, ((MismatchedInputException) cause).getPath().get(0).getFieldName()));
        } else {
            // Check if we are missing the body as a whole
            if (e.getLocalizedMessage().startsWith("Required request body is missing")) {
                return errorHandler.handleStatusCodeException(new EmptyElementException(RoutingErrorCodes.MISSING_PARAMETER, "Request body could not be read"));
            }
            return errorHandler.handleGenericException(e);
        }
    }

    @ExceptionHandler(StatusCodeException.class)
    public ResponseEntity<Object> handleException(final StatusCodeException e) {
        return errorHandler.handleStatusCodeException(e);
    }
}
