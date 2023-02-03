cmake_minimum_required(VERSION 3.4.1)
project(xa_native)

add_subdirectory(../../../../mmkv mmkv)
add_subdirectory(../../../../dexkit/DexKit/Core dex_kit_static)

add_library(xa_native SHARED
        native-lib.cpp)

find_library(log-lib log)

target_link_libraries(xa_native
        mmkv
        dex_kit_static
        z
        ${log-lib})