cmake_minimum_required(VERSION 3.4.1)
project(xa_native)

add_subdirectory(../../../../mmkv mmkv)

add_library(xa_native SHARED
        dexkit.cpp
        native-lib.cpp)

find_library(log-lib log)
find_package(dexkit REQUIRED CONFIG)

target_link_libraries(xa_native
        mmkv
        dexkit::dex_kit_static
        z
        ${log-lib})

if (NOT DEFINED DEBUG_SYMBOLS_PATH)
    set(DEBUG_SYMBOLS_PATH ${CMAKE_BINARY_DIR}/symbols)
endif()

add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E make_directory ${DEBUG_SYMBOLS_PATH}/${ANDROID_ABI}
        COMMAND ${CMAKE_OBJCOPY} --only-keep-debug $<TARGET_FILE:${PROJECT_NAME}>
        ${DEBUG_SYMBOLS_PATH}/${ANDROID_ABI}/${PROJECT_NAME}
        COMMAND ${CMAKE_STRIP} --strip-all $<TARGET_FILE:${PROJECT_NAME}>)