    package com.example.bottomnavi;

    import android.Manifest;
    import android.content.Context;
    import android.content.pm.PackageInfo;
    import android.content.pm.PackageManager;
    import android.content.pm.Signature;
    import android.os.Build;
    import android.os.Bundle;
    import android.util.Base64;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.EditText;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.core.content.ContextCompat;
    import androidx.fragment.app.Fragment;

    import com.example.bottomnavi.frag4_place.KakaoMapService;
    import com.example.bottomnavi.frag4_place.Place;

    import net.daum.mf.map.api.MapPOIItem;
    import net.daum.mf.map.api.MapPoint;
    import net.daum.mf.map.api.MapView;
    import java.security.MessageDigest;
    import java.security.NoSuchAlgorithmException;
    import java.util.List;

    public class Frag4 extends Fragment {
        private MapView mapView;
        private ViewGroup mapViewContainer;
        private MapPOIItem customMarker;
        private EditText searchEditText;
        private boolean isFirstLocationUpdate = true;
        private KakaoMapService mapService;
        @Nullable

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.frag4, container, false);

            // 프래그먼트 내에서 액티비티의 컨텍스트를 가져옵니다.
            Context context = requireContext();

            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    Log.d("키해시는 :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
            } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            // 권한ID 를 가져옵니다
            int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
            int permission2 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            int permission3 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

            // 권한이 열려있는지 확인
            if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED || permission3 == PackageManager.PERMISSION_DENIED) {
                // 마쉬멜로우 이상버전부터 권한을 물어본다
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 권한 체크(READ_PHONE_STATE 의 requestCode 를 1000으로 세팅)
                    requestPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
                }
            } else {
                // 권한이 허용된 경우 지도를 띄움
                mapView = new MapView(context);
                mapViewContainer = rootView.findViewById(R.id.map_view);
                mapViewContainer.addView(mapView);
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            }
            searchEditText = rootView.findViewById(R.id.search_edittext);
            Button searchButton = rootView.findViewById(R.id.search_button);
            // KakaoMapService 객체 초기화
            mapService = new KakaoMapService(requireContext());

            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String searchQuery = searchEditText.getText().toString();
                    if (!searchQuery.isEmpty()) {
                        // 검색어로 위치 검색
                        searchLocation(searchQuery);
                    }
                }
            });
            /*
            // 검색 버튼 클릭 이벤트 처리
            rootView.findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 검색어를 가져와서 카카오 지도에서 해당 위치로 이동
                    String searchQuery = ((EditText) rootView.findViewById(R.id.search_edittext)).getText().toString();
                    if (!searchQuery.isEmpty()) {
                        searchLocation(searchQuery);
                    }
                }
            });             */

            return rootView;
        }
        // 내 위치를 지도 위에 표시하는 메서드
        private void showMyLocation(double latitude, double longitude) {
            if (mapView != null) {
                MapPoint MARKER_POINT = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                if (customMarker == null) {
                    customMarker = new MapPOIItem();
                    customMarker.setItemName("내 위치");
                    customMarker.setTag(1);
                    customMarker.setMapPoint(MARKER_POINT);
                    customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                    customMarker.setCustomImageResourceId(R.drawable.custom_marker_red);
                    customMarker.setCustomImageAutoscale(false);
                    customMarker.setCustomImageAnchor(0.5f, 1.0f);
                    mapView.addPOIItem(customMarker);
                } else {
                    customMarker.setMapPoint(MARKER_POINT);
                }

                mapView.setMapCenterPoint(MARKER_POINT, true);
            }
        }
        // 추가: 사용자가 입력한 검색어로 위치 검색
        private void searchLocation(String query) {
            // 사용자의 검색어를 이용하여 카카오 지도에서 해당 위치 검색 및 이동
            mapService.searchPlace("904e8602dd13516eb005f4c980a95ec2", query, new KakaoMapService.OnPlaceSearchListener() { //rest api
                @Override
                public void onPlaceSearchSuccess(List<Place> places) {
                    if (places.size() > 0) {
                        // 검색 결과에서 장소 정보를 가져오고 마커로 표시
                        for (Place place : places) {
                            double latitude = Double.parseDouble(place.getY());
                            double longitude = Double.parseDouble(place.getX());
                            String placeName = place.getPlaceName(); // 인스턴스에서 메서드 호출
                            // 장소의 위치에 마커 표시
                            showLocationOnMap(placeName, latitude, longitude);
                        }
                    }
                }

                @Override
                public void onPlaceSearchError(String errorMessage) {
                    // Handle search error
                }
            });
        }
        // 권한 체크 이후 로직을 여기에 추가할 수 있습니다.
        /*
        private void searchLocation(String query) {
            // 검색어를 이용하여 카카오 지도에서 해당 위치 검색 및 이동
            Uri uri = Uri.parse("kakaomap://search?q=" + query);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }*/

        // 지도에 장소 위치를 마커로 표시
        private void showLocationOnMap(String name, double latitude, double longitude) {
            if (mapView != null) {
                MapPoint location = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                MapPOIItem marker = new MapPOIItem();
                marker.setItemName(name);
                marker.setTag(0);
                marker.setMapPoint(location);
                marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                marker.setCustomImageResourceId(R.drawable.custom_marker_red);
                marker.setCustomImageAnchor(0.5f, 1.0f);

                mapView.addPOIItem(marker);
            }
        }
    }
