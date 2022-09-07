cmake_minimum_required(VERSION 3.4.1)
project(xa_native)

find_package(cxx REQUIRED CONFIG)
link_libraries(cxx::cxx)

add_definitions(-std=c++20)

add_subdirectory(../../../../mmkv mmkv)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/DexKit)

add_library( # Sets the name of the library.
        xa_native

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp
        )

find_library(
        log-lib
        log)

target_link_libraries( # Specifies the target library.
        xa_native
#        mmkv
        dex_kit_static
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        )

if (NOT DEFINED DEBUG_SYMBOLS_PATH)
    set(DEBUG_SYMBOLS_PATH ${CMAKE_BINARY_DIR}/symbols)
endif()

add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E make_directory ${DEBUG_SYMBOLS_PATH}/${ANDROID_ABI}
        COMMAND ${CMAKE_OBJCOPY} --only-keep-debug $<TARGET_FILE:${PROJECT_NAME}>
        ${DEBUG_SYMBOLS_PATH}/${ANDROID_ABI}/${PROJECT_NAME}
        COMMAND ${CMAKE_STRIP} --strip-all $<TARGET_FILE:${PROJECT_NAME}>)