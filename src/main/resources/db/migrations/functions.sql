create or replace function group_by_engine_power()
returns table (engine_power BIGINT, object_count BIGINT) as $$
    begin
        return query
        select engine_power, count(*)
        from s367595.is_vehicle
        group by engine_power
        order by engine_power;
    end;
$$ language plpgsql;

create or replace function count_by_fuel_consumption(p_fuel_consumption numeric)
returns int as $$
    begin
        return (
            select count(*)
            from s367595.is_vehicle
            where fuel_consumption = p_fuel_consumption
        );
    end;
$$ language plpgsql;

create or replace function count_by_fuel_type_less_than(p_fuel_type text)
returns int as $$
    begin
        return (
            select count(*)
            from s367595.is_vehicle
            where fuel_type < p_fuel_type
        );
    end;
$$ language plpgsql;

create or replace function find_by_engine_power_range(p_min_power int, p_max_power int)
returns setof s367595.is_vehicle as $$
    begin
        return query
        select *
        from s367595.is_vehicle as v
        where engine_power between p_min_power and p_max_power
        order by v.id;
    end;
$$ language plpgsql;

create or replace function find_by_wheel_count_range(p_min_number int, p_max_number int)
returns setof s367595.is_vehicle as $$
    begin
        return query
        select *
        from s367595.is_vehicle as v
        where number_of_wheels between p_min_number and p_max_number
        order by v.id;
    end;
$$ language plpgsql;