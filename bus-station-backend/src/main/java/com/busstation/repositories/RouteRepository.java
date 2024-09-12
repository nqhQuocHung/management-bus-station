package com.busstation.repositories;

import com.busstation.pojo.Route;

import java.rmi.MarshalledObject;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RouteRepository  {
   List<Route> list(Map<String, String> params);
   Long count(Map<String, String> params);
   Route getById(Long id);
   void save(Route route);
   List<Route> findByCompanyId(Long companyId);
   Optional<Route> findById(Long id);
}
