requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo") { 
  val fieldsInit = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo/django/db/models/fields/__init__.py")
  val content = fieldsInit.read()
  // Get original content back to work from
  val originalContent = """import collections.abc
import copy
import datetime
import decimal
import operator
import uuid
import warnings
from base64 import b64decode, b64encode
from functools import partialmethod, total_ordering

from django import forms
from django.apps import apps
from django.conf import settings
from django.core import checks, exceptions, validators
# When the _meta object was formalized, this exception was moved to
# django.core.exceptions. It is retained here for backwards compatibility
# purposes.
from django.core.exceptions import FieldDoesNotExist  # NOQA
from django.db import connection, connections, router
from django.db.models.constants import LOOKUP_SEP
from django.db.models.query_utils import DeferredAttribute, RegisterLookupMixin
from django.utils import timezone
from django.utils.datastructures import DictWrapper
from django.utils.dateparse import (
    parse_date, parse_datetime, parse_duration, parse_time,
)
from django.utils.duration import duration_microseconds, duration_string
from django.utils.functional import Promise, cached_property
from django.utils.ipv6 import clean_ipv6_address
from django.utils.itercompat import is_iterable
from django.utils.text import capfirst
from django.utils.translation import gettext_lazy as _

__all__ = [
    'AutoField', 'BLANK_CHOICE_DASH', 'BigAutoField', 'BigIntegerField',
    'BinaryField', 'BooleanField', 'CharField', 'CommaSeparatedIntegerField',
    'DateField', 'DateTimeField', 'DecimalField', 'DurationField',
    'EmailField', 'Empty', 'Field', 'FieldDoesNotExist', 'FilePathField',
    'FloatField', 'GenericIPAddressField', 'IPAddressField', 'IntegerField',
    'NOT_PROVIDED', 'NullBooleanField', 'PositiveIntegerField',
    'PositiveSmallIntegerField', 'SlugField', 'SmallAutoField',
    'SmallIntegerField', 'TextField', 'TimeField', 'URLField', 'UUIDField',
]


class Empty:
    pass


class NOT_PROVIDED:
    pass


# The values to use for "blank" in SelectFields. Will be appended to the start
# of most "choices" lists.
BLANK_CHOICE_DASH = [("", "---------")]


def _load_field(app_label, model_name, field_name):
    return apps.get_model(app_label, model_name)._meta.get_field(field_name)


# A guide to Field parameters:
#
#   * name:      The name of the field specified in the model.
#   * attname:   The attribute to use on the model object. This is the same as
#                "name", except in the case of ForeignKeys, where "_id" is
#                appended.
#   * db_column: The db_column specified in the model (or None).
#   * column:    The database column for this field. This is the same as
#                "attname", except if db_column is specified.
#
# Code that introspects values, or does other dynamic things, should use
# attname. For example, this gets the primary key value of object "obj":
#
#     getattr(obj, opts.pk.attname)

def _empty(of_cls):
    new = Empty()
    new.__class__ = of_cls
    return new


def return_None():
    return None


@total_ordering
class Field(RegisterLookupMixin):
    """Base class for all field types"""

    # Designates whether empty strings fundamentally are allowed at the
    # database level.
    empty_strings_allowed = True
    empty_values = list(validators.EMPTY_VALUES)

    # These track each time a Field instance is created. Used to retain order.
    # The auto_creation_counter is used for fields that Django implicitly
    # creates, creation_counter is used for all user-specified fields.
    creation_counter = 0
    auto_creation_counter = -1
    default_validators = []  # Default set of validators
    default_error_messages = {
        'invalid_choice': _('Value %(value)r is not a valid choice.'),
        'null': _('This field cannot be null.'),
        'blank': _('This field cannot be blank.'),
        'unique': _('%(model_name)s with this %(field_label)s '
                    'already exists.'),
        # Translators: The 'lookup_type' is one of 'date', 'year' or 'month'.
        # Eg: "Title must be unique for pub_date year"
        'unique_for_date': _("%(field_label)s must be unique for "
                             "%(date_field_label)s %(lookup_type)s."),
    }
    system_check_deprecated_details = None
    system_check_removed_details = None

    # Field flags
    hidden = False

    many_to_many = None
    many_to_one = None
    one_to_many = None
    one_to_one = None
    related_model = None

    descriptor_class = DeferredAttribute

    # Generic field type description, usually overridden by subclasses
    def _description(self):
        return _('Field of type: %(field_type)s') % {
            'field_type': self.__class__.__name__
        }
    description = property(_description)

    def __init__(self, verbose_name=None, name=None, primary_key=False,
                 max_length=None, unique=False, blank=False, null=False,
                 db_index=False, rel=None, default=NOT_PROVIDED, editable=True,
                 serialize=True, unique_for_date=None, unique_for_month=None,
                 unique_for_year=None, choices=None, help_text='', db_column=None,
                 db_tablespace=None, auto_created=False, blank_choice=None,
                 description=None, db_comment=None, **kwargs):
        self.name = name
        self.verbose_name = verbose_name
        self.primary_key = primary_key
        self.max_length = max_length
        self.unique = unique
        self.blank = blank
        self.null = null
        self.db_index = db_index
 rel
        self.default = default
        self.editable        self.rel = = editable
        self.serialize = serialize
        self.unique_for_date = unique_for_date
        self.unique_for_month = unique_for_month
        self.unique_for_year = unique_for_year
        if isinstance(choices, collections.abc.Iterator):
            choices = list(choices)
        self.choices = choices
        self.help_text = help_text
        self.db_index = db_index
        self.db_column = db_column
        self._db_tablespace = db_tablespace
        self.auto_created = auto_created
        self.description = description
        self.db_comment = db_comment

        # Adjust the appropriate creation counter, and save our local copy.
        if auto_created:
            Field.auto_creation_counter -= 1
            self.creation_counter = Field.auto_creation_counter
        else:
            Field.creation_counter += 1
            self.creation_counter = Field.creation_counter

        self._validators = list(self.default_validators)

    def __repr__(self):
        return '<%s: %s>' % (self.__class__.__name__, self.name)

    def __str__(self):
        return self.description

    def __eq__(self, other):
        # Need to match types due to the existence of proxy fields.
        return (
            isinstance(other, Field) and
            self.name == other.name and
            (self.model._meta.app_label == other.model._meta.app_label) and
            # Check that the parent is not an ancestor. This is the case where
            # a field inherits from the parent model.
            self.model._meta.pk != self and
            # We must check that the remote field is not inherited from an
            # ancestor of the parent.
            not self.remote_field.model._meta.proxy and
            # It is possible to create a field directly from a field, e.g.
            # when you inherit from the field. It must be added to the options
            # that can be copied in the clone process.
            getattr(self, 'model', None) == getattr(other, 'model', None)
        )

    def __hash__(self):
        return hash(self.name)

    @property
    def attname(self):
        return self.name

    @property
    def _pk_field(self):
        return self

    def get_pk_value_on_save(self, instance):
        if self.primary_key and self.default:
            return self.default
        return getattr(instance, self.attname)

    @property
    def model(self):
        return self.__dict__.get('model')

    @model.setter
    def model(self, model):
        self.__dict__['model'] = model

    def _check_choices(self):
        if not self.choices:
            return []

        def is_value(value, accept_promise=True):
            return isinstance(value, (str, Promise) if accept_promise else str) or not is_iterable(value)

        if is_value(self.choices, accept_promise=False):
            return [
                checks.Error(
                    "'choices' must be an iterable (e.g., a list or tuple).",
                    obj=self,
                    id='fields.E004',
                )
            ]

        # Expect [group_name, [value, display]]
        for choices_group in self.choices:
            try:
                group_name, group_choices = choices_group
            except (TypeError, ValueError):
                # Containing non-pairs
                break
            try:
                if not all(
                    is_value(value) and is_value(human_name)
                    for value, human_name in group_choices
                ):
                    break
            except (TypeError, ValueError):
                # No groups, choices in the form [value, display]
                value, human_name = group_name, group_choices
                if not is_value(value) or not is_value(human_name):
                    break

            # Special case: choices=['ab']
            if isinstance(choices_group, str):
                break
        else:
            return []

        return [
            checks.Error(
                "'choices' must be an iterable containing "
                "(actual value, human readable name) tuples.",
                obj=self,
                id='fields.E005',
            )
        ]

    def _check_db_index(self):
        if self.db_index not in (None, True, False):
            return [
                checks.Error(
                    "'db_index' must be None, True or False.",
                    obj=self,
                    id='fields.E006',
                )
            ]
        return []

    def _check_db_index_not_nullable(self, connection):
        if (
            self.null and
            not self.empty_strings_allowed and
            self.db_index and
            not connection.features.interprets_empty_strings_as_nulls
        ):
            return [
                checks.Warning(
                    'db_index=True is ignored on a nullable field.',
                    obj=self,
                    id='fields.W164',
                )
            ]
        return []

    def _check_unique(self):
        if not self.unique:
            return []
        elif self.primary_key:
            return [
                checks.Warning(
                    'Setting unique=True on a primary key field has no effect.',
                    obj=self,
                    id='fields.W161',
                )
            ]
        elif self.remote_field:
            return [
                checks.Warning(
                    'unique=True is ignored when a field has a remote field.',
                    obj=self,
                    id='fields.W162',
                )
            ]
        return []

    def _check_primary_key(self):
        if self.primary_key and self.remote_field:
            return [
                checks.Error(
                    'A primary key field cannot have a remote field.',
                    obj=self,
                    id='fields.E100',
                )
            ]
        return []

    def _check_related_model(self):
        if self.remote_field and not self.remote_field.model:
            return [
                checks.Error(
                    'Field defines a relation but does not define a model.',
                    obj=self,
                    id='fields.E101',
                )
            ]
        return []

    def _check_on_delete(self):
        if (
            self.remote_field and
            self.remote_field.on_delete is None and
            # Make sure not to be nested in another check function (e.g. in
            # ForeignKey.check() which calls super().check())
            getattr(self, 'check', None) == Field.check
        ):
            return [
                checks.Error(
                    'on_delete must be a callable or a value in (%s).' % (
                        ', '.join(c for c in models.CASCADE if c != models.CASCADE[0])
                    ),
                    obj=self,
                    id='fields.E102',
                )
            ]
        return []

    def _check_validators(self):
        if self.validators:
            return []
        return []

    def _check_allowed_field_lookups(self):
        if hasattr(self, 'allow_relation'):
            return []
        return []

    def _check_deprecation_details(self):
        if self.system_check_deprecated_details is None:
            return []
        return [
            checks.Warning(
                **self.system_check_deprecated_details,
                obj=self,
            )
        ]

    def _check_removed_details(self):
        if self.system_check_removed_details is None:
            return []
        return [
            checks.Error(
                **self.system_check_removed_details,
                obj=self,
            )
        ]

    def check(self, **kwargs):
        return [
            *self._check_choices(),
            *self._check_db_index(),
            *self._check_db_index_not_nullable(kwargs.get('connection', None)),
            *self._check_unique(),
            *self._check_primary_key(),
            *self._check_related_model(),
            *self._check_on_delete(),
            *self._check_validators(),
            *self._check_allowed_field_lookups(),
            *self._check_deprecation_details(),
            *self._check_removed_details(),
        ]

    def get_col(self, alias):
        return Col(alias, self)

    def get_database_name(self):
        if self.db_comment:
            try:
                comment = self.db_comment._proxy____cast()
            except AttributeError:
                comment = self.db_comment
            return comment
        else:
            return None

    def get_choices(
        self, include_blank=True, blank_choice=BLANK_CHOICE_DASH, limit_choices_to=None, ordering=(),
    ):
        """
        Return choices with a default blank choices included, for use
        as <select> choices for this field.
        """
        if self.choices is not None:
            choices = list(self.choices)
            if include_blank:
                blank_present_in_choices = any(
                    choice[0] in ('', None) for choice in choices
                    if choice and isinstance(choice, (list, tuple))
                )
                if not blank_present_in_choices:
                    choices = list(blank_choice) + choices
            return choices
        rel_model = self.remote_field.model
        rel_field = self.remote_field.get_related_field()
        if limit_choices_to:
            limit_choices_to = limit_choices_to.copy()
        qs = rel_model._default_manager.using(self.db_alias).filter(**limit_choices_to or {})
        if ordering:
            qs = qs.order_by(*ordering)
        return ([blank_choice] if include_blank else []) + [
            (getattr(x, rel_field.attname), str(x))
            for x in qs
        ]

    def get_choices_reverse(self):
        """Yield (human_name, value) tuples, instead of (value, human_name)."""
        choices = self.get_choices(include_blank=False)
        return list(reversed(choices))

    def value_to_string(self, obj):
        return str(self.value_from_object(obj))

    def _get_val_from_obj(self, obj):
        return obj.__dict__[self.attname]

    def value_from_object(self, obj):
        if obj is None:
            return None
        return getattr(obj, self.attname)

    def _get_display(self, obj, value):
        """
        Return display representation of field's value.
        """
        return force_str(dict(self.flatchoices).get(value, value), strings_only=True)

    def _check_null(self, connection):
        if (
            self.null and
            not self.empty_strings_allowed and
            connection.features.interprets_empty_strings_as_nulls
        ):
            return [
                checks.Warning(
                    'null=True is ignored when a field has a remote field.',
                    obj=self,
                    id='fields.W163',
                )
            ]
        return []

    def get_prep_value(self, value):
        """Perform preliminary non-db specific value checks and conversions."""
        if isinstance(value, Promise):
            value = value._proxy____cast()
        return value

    def get_db_prep_value(self, value, connection, prepared=False):
        """
        Return field's value prepared for interacting with the database backend.

        Used by the default implementations of get_db_prep_save().
        """
        if not prepared:
            value = self.get_prep_value(value)
        return value

    def get_db_prep_save(self, value, connection):
        """Return field's value prepared for saving to a database."""
        return self.get_db_prep_value(value, connection)

    def get_internal_type(self):
        return self.__class__.__name__

    def get_meta_type(self, connection):
        """
        Return the underlying database column type for the field.

        As some databases differ in their requirements for the underlying
        column type, it is necessary to wrap the value before it is
        processed by the database. Therefore, this method returns the
        value of the field wrapped in the database's specific wrapper class.
        """
        # Return the database type for the field.
        return connection.data_types.get(self.get_internal_type(), self.get_internal_type())

    def pre_save(self, model_instance, add):
        """Returns the value of this field from the model instance."""
        return getattr(model_instance, self.attname)

    def get_default(self):
        """Return the default value for this field."""
        return self.default

    def get_related_model(self):
        """Return the model this field relates to. """
        return self.remote_field.model if self.remote_field else None

    def get_attname(self):
        return self.name

    def get_attname_column(self):
        attname = self.get_attname()
        column = self.db_column or attname
        return attname, column

    def get_alias(self):
        """
        Return the alias for the field's database column, not including the
        model's table name.
        """
        if self.remote_field:
            return self.remote_field.get_accessor_name()
        return self.db_column or self.name

    def get_column_name(self):
        """
        Return the database column name for this field, not including the
        model's table name.
        """
        return self.db_column or self.name

    def get_db_column_name(self):
        """
        Return the database column name for this field, not including the
        model's table name. Set db_column if not already set.
        """
        return self.db_column or self.get_attname_column()[1]

    def get_index_name(self):
        """
        Return the name of the related index by using the value of
        get_index_name_ex().
        """
        return self.get_index_name_ex()[0]

    def get_index_name_ex(self, connection=None):
        """
        Return the name of the index for this field, not including the
        model's table name.
        """
        if self.primary_key:
            return None, None
        if self.remote_field:
            return self.remote_field.get_accessor_name(), None
        connection = connection or router.db_for_read(self.model)
        name = self.model._meta.db_table + '_' + self.get_column_name()
        return (
            connection.ops.quote_name(name)[: self.max_length],
            connection.ops.quote_name(name)[: self.max_length],
        )

    def get_pk_index_name(self):
        """
        Return the name of the primary key index for this field, not
        including the model's table name.
        """
        return self.model._meta.pk.get_index_name()

    def has_default(self):
        """Return a boolean of whether this field has a default value."""
        return self.default is not NOT_PROVIDED

    def get_default(self):
        """Return the default value for this field."""
        if self.default is not NOT_PROVIDED:
            return self.default
        if not self.empty_strings_allowed or self.null:
            return None
        return ""

    def get_verbose_name(self):
        """
        Return the verbose name of the field, either from ugettext or
        custom definition. Append a blank space if there is one.
        """
        if self.verbose_name is None:
            name = self.name.replace('_', ' ')
            verbose_name = name.capitalize()
        else:
            verbose_name = self.verbose_name
        return verbose_name + ' ' if self.help_text else verbose_name

    def set_attributes_from_name(self, name):
        self.name = name
        self.attname = self.get_attname()
        if self.verbose_name is None:
            self.verbose_name = self.name.replace('_', ' ')

    def contribute_to_class(self, cls, name, private_only=False):
        self.set_attributes_from_name(name)
        self.model = cls
        cls._meta.add_field(self)
        if self.remote_field:
            self.remote_field.set_field_name()
        if not self._meta.has_default_field:
            if self.has_default():
                self._meta.has_default_field = self
        if self.primary_key:
            self._meta.pk = self
        if self.unique:
            self._meta.unique_fields.append(self)
        if self.serialize:
            if not hasattr(self, '_get_val_from_obj'):
                self._get_val_from_obj = self._check_default
                # We set _get_val_from_obj after contribute_to_class
                # because we need the model.

    def _check_default(self, obj):
        if obj.__dict__.get(self.attname) is None and self.has_default():
            return self.get_default()
        return obj.__dict__.get(self.attname, self.get_default())

    def get_validation_exclusions(self):
        """
        Return a list of field names to exclude from validation when
        validating a model.
        """
        exclusions = []
        for field in self._meta.parents.values():
            exclusions.append(field.name)
        if hasattr(self, 'remote_field'):
            if self.remote_field:
                exclusions.append(self.remote_field.field_name)
                if self.remote_field.model:
                    # Move towards getting only the concrete fields
                    # instead of the proxy fields.
                    exclusions.extend(
                        f.name
                        for f in self.remote_field.model._meta.get_fields()
                        if f.is_field
                    )
        return exclusions

    def validate(self, value, model_instance):
        """
        Validate value and raise ValidationError if necessary. This method
        should be overriden to add custom validation logic.
        """
        if value is None and not self.null:
            raise ValidationError(self.error_messages['null'], code='null')
        if not value and self.blank:
            return
        if value is None and self.null:
            return
        if self.choices is not None and value not in self.empty_values:
            for option_key, option_value in self.choices:
                if value == option_key:
                    return
            raise ValidationError(
                self.error_messages['invalid_choice'],
                code='invalid_choice',
                params={'value': value},
            )

    def run_validators(self, value):
        if value in self.empty_values:
            return value
        value = self.to_python(value)
        kwargs = {}
        for validator in self.validators:
            if hasattr(validator, 'limit_value'):
                kwargs = {'limit_value': validator.limit_value}
            validator(value, **kwargs)
        return value

    def to_python(self, value):
        """Convert the input value into the expected Python data type."""
        return value

    def clean(self, value, model_instance):
        """
        Convert the value passed to get_prep_value() and get_db_prep_save()
        to an object that may be used as a model field's value.

        Perform any secondary conversion. Do not convert escalates,
        form validation, or lookups.
        """
        return value

    def db_type(self, connection):
        """Return the database column type for this field."""
        return connection.data_types[self.get_internal_type()]

    def db_parameters(self, connection):
        type_string = self.db_type(connection)
        return {
            'type': type_string,
            'check': self.db_check(connection),
        }

    def db_check(self, connection):
        return None

    def db_collation(self, connection):
        return self.collation

    def cast_db_type(self, connection):
        return self.db_type(connection)

    def get_placeholder(self, value, connection):
        """
        Return the placeholder for the given value.
        """
        return '%s'

    def get_db_converted_attribute(self, connection):
        if self.has_default():
            default = self.default
            if callable(default):
                default = default()
            return default
        return None

    def select_format(self, compiler, sql, params):
        """
        Custom format for SELECT clause. Default to None.
        """
        return sql

    def as_sql(self, compiler, connection):
        """
        Compile the SQL for this field, including any custom parameters or
        transformations. This is used for filtering values and for
        creating constraints and/or indexes.

        This is not used for serialization.
        """
        return compiler.compile(self)

    def as_mysql(self, compiler, connection):
        """
        Build the column definition for MySQL.
        """
        sql = self.db_type(connection)
        # No support for Check constraints.
        return sql, []

    def as_oracle(self, compiler, connection):
        """
        Build the column definition for Oracle.
        """
        if not self.null:
            sql = "%s NOT NULL" % self.db_type(connection)
        else:
            sql = self.db_type(connection)
        # Check constraints aren't supported for Oracle.
        return sql, []

    def cast_char_field_without_max_length(self, connection):
        return connection.ops.cast_char_field_without_max_length

    def get_constraints(self):
        constraints = []
        if not self.null and not self.primary_key:
            constraints.append(
                Check(
                    check=~Q(**{'%s__isnull' % self.name: True}),
                    name=self.get_column_name()[0:128] + '_is_null',
                )
            )
        if self.choices:
            constraints.append(
                Check(
                    check=Q(**{self.name: [c[0] for c in self.choices]}),
                    name=self.get_column_name()[0:128] + '_is_choice',
                )
            )
        return constraints

    def get_identity(self):
        """Return a tuple identifying this field."""
        return (
            self.__class__,
            self.name,
            self.model._meta.app_label,
            self.model._meta.model_name,
        )

    def __getstate__(self):
        return {
            **self.__dict__,
            'model': None,
            ' validators': self._validators,
        }

    def __setstate__(self, state):
        self.__dict__.update(state)
        self._validators = state.get(' validators', [])

    def __copy__(self):
        # We need to reset the creation counter for this copy.
        return self._clone()

    def _clone(self):
        """
        Return a copy of this field.
        """
        # Create a new empty object.
        new = object.__new__(self.__class__)
        # Save all the attributes of this field.
        new.__dict__ = self.__dict__.copy()
        # Make sure the choices are converted to list of tuples.
        if isinstance(new.choices, collections.abc.Iterator):
            new.choices = list(new.choices)
        # Reset the creation counter.
        Field.creation_counter += 1
        new.creation_counter = Field.creation_counter
        # Make sure max_length is an int (if it's not a string).
        if new.max_length:
            new.max_length = int(new.max_length)
        # Copy validators instead of mutating.
        new._validators = self._validators.copy()
        return new

    def _set_attributes(self, kwargs):
        """
        Set attributes from kwargs, including common ones, like 'validators'.
        """
        all_kwargs = {
            'validators': validators,
            'db_index': False,
        }
        all_kwargs.update(kwargs)
        # A1. Extract common attributes.
        for attr, value in all_kwargs.items():
            setattr(self, attr, value)

    def _add_validators(self, validators):
        self._validators.extend(validators)

    @cached_property
    def validators(self):
        """Collect validators from validators and from the field itself."""
        # validators is a lazy relation. self._validators is a list of
        # validators.
        return list(self._validators) + self._check_choices()

    def _check_choices(self):
        if not self.choices:
            return []

        def is_value(value, accept_promise=True):
            return isinstance(value, (str, Promise) if accept_promise else str) or not is_iterable(value)

        if is_value(self.choices, accept_promise=False):
            return [
                checks.Error(
                    "'choices' must be an iterable (e.g., a list or tuple).",
                    obj=self,
                    id='fields.E004',
                )
            ]

        # Expect [group_name, [value, display]]
        for choices_group in self.choices:
            try:
                group_name, group_choices = choices_group
            except (TypeError, ValueError):
                # Containing non-pairs
                break
            try:
                if not all(
                    is_value(value) and is_value(human_name)
                    for value, human_name in group_choices
                ):
                    break
            except (TypeError, ValueError):
                # No groups, choices in the form [value, display]
                value, human_name = group_name, group_choices
                if not is_value(value) or not is_value(human_name):
                    break

            # Special case: choices=['ab']
            if isinstance(choices_group, str):
                break
        else:
            return []

        return [
            checks.Error(
                "'choices' must be an iterable containing "
                "(actual value, human readable name) tuples.",
                obj=self,
                id='fields.E005',
            )
        ]

    @property
    def empty_values(self):
        return list(validators.EMPTY_VALUES)

    def get_bound_field(self, form, bound_data):
        """
        Return a BoundField instance for this field.
        """
        return BoundField(self, form, bound_data)

    def info(self):
        return Info(
            model=self.model._meta.label,
            field=self.name,
        )

    def contribute_to_related_class(self, model, related):
        pass


class CharField(StringFieldMixin, Field):
    description = _("String (up to %(max_length)s)")

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.validators.append(validators.MaxLengthValidator(self.max_length))

    def check(self, **kwargs):
        return [
            *super().check(**kwargs),
            *self._check_max_length_attribute(**kwargs),
            *self._check_max_length_choices(**kwargs),
        ]

    def _check_max_length_attribute(self, **kwargs):
        if self.max_length is None:
            return [
                checks.Error(
                    "CharFields must define a 'max_length' attribute.",
                    obj=self,
                    id='fields.E120',
                )
            ]
        elif (not isinstance(self.max_length, int) or isinstance(self.max_length, bool) or
                self.max_length <= 0):
            return [
                checks.Error(
                    "'max_length' must be a positive integer.",
                    obj=self,
                    id='fields.E121',
                )
            ]
        else:
            return []

    def _check_max_length_choices(self):
        if not self.choices:
            return []

        if self.max_length is None:
            return []

        if not isinstance(self.max_length, int) or isinstance(self.max_length, bool) or self.max_length <= 0:
            return []

        max_choice_length = 0
        for choice in self.choices:
            try:
                group_name, group_choices = choice
                if isinstance(group_choices, (list, tuple)):
                    for value, _ in group_choices:
                        if value is None:
                            continue
                        value_str = str(value) if isinstance(value, Promise) else str(value)
                        max_choice_length = max(max_choice_length, len(value_str))
                else:
                    value = group_name
                    value_str = str(value) if isinstance(value, Promise) else str(value)
                    max_choice_length = max(max_choice_length, len(value_str))
            except (TypeError, ValueError):
                value, _ = choice
                if value is None:
                    continue
                value_str = str(value) if isinstance(value, Promise) else str(value)
                max_choice_length = max(max_choice_length, len(value_str))

        if max_choice_length > self.max_length:
            return [
                checks.Error(
                    "max_length must be at least %d to accommodate the longest choice value (%d)." % (max_choice_length, max_choice_length),
                    obj=self,
                    id='fields.E122',
                )
            ]
        return []

    def cast_db_type(self, connection):
        if self.max_length is None:
            return connection.ops.cast_char_field_without_max_length
        return super().cast_db_type(connection)

    def get_internal_type(self):
        return "CharField"

    def to_python(self, value):
        if isinstance(value, str) or value is None:
            return value
        return str(value)

    def get_prep_value(self, value):
        value = super().get_prep_value(value)
        return self.to_python(value)

    def formfield(self, **kwargs):
        # Passing max_length to forms.CharField means that the value's length
        # will be validated twice. This is considered acceptable since we want
        # the value in the form field (to pass into widget for example).
        defaults = {'max_length': self.max_length}
        # TODO: Handle multiple backends with different feature flags.
        if self.null and not connection.features.interprets_empty_strings_as_nulls:
            defaults['empty_value'] = None
        defaults.update(kwargs)
        return super().formfield(**defaults)
"""
  
  fieldsInit.write(originalContent)
  println("File restored with correct code")
}