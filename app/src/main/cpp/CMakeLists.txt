cmake_minimum_required(VERSION 3.4.1)
project(xa_native)

add_subdirectory(../../../../mmkv mmkv)
add_subdirectory(../../../../dexkit/DexKit/Core dexkit_static)

add_library(crypto STATIC IMPORTED)
set_target_properties(
        crypto
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/../../../../openssl/lib/${ANDROID_ABI}/libcrypto.a
)

add_library(openssl STATIC IMPORTED)
set_target_properties(
        openssl
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/../../../../openssl/lib/${ANDROID_ABI}/libssl.a
)

add_library(
        ${PROJECT_NAME} SHARED
        md5.cpp
        base64.cpp
        native-lib.cpp
        ../../../../dexkit/DexKit/dexkit/src/main/cpp/native-bridge.cpp
)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../../../../openssl/include)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

find_library(log-lib log)

target_link_libraries(xa_native
        crypto
        openssl
        mmkv
        dexkit_static
        z
        ${log-lib})
